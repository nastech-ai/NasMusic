#!/usr/bin/env python3
"""
Push NasMusic to nastech-ai/NasMusic via GitHub API.
Auto-resumes after rate limits. Checkpoints progress so restarts continue
from where they left off.
"""
import os, sys, json, base64, hashlib, time, requests
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed

TOKEN  = os.environ["GITHUB_PERSONAL_ACCESS_TOKEN"]
OWNER  = "nastech-ai"
REPO   = "NasMusic"
BASE   = f"https://api.github.com/repos/{OWNER}/{REPO}"
ROOT   = Path("/home/runner/workspace")
CKPT   = ROOT / ".push_checkpoint.json"   # saves uploaded SHAs between runs

SESSION = requests.Session()
SESSION.headers.update({
    "Authorization": f"token {TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "Content-Type": "application/json",
})

SKIP_DIRS = {
    ".git", "build", ".gradle", "actions-runner", "__pycache__",
    ".local", "captures", ".idea", "intermediates", "generated",
    "ffmpeg-kit-16KB", "rococoa", "android-youtubeExtractor", "lyricsplus",
    ".pythonlibs", ".upm", ".cache", "node_modules", ".npm",
}
SKIP_EXT = {".class", ".pyc", ".pyo", ".so", ".a", ".o", ".dll", ".lib", ".dylib", ".exe", ".bin"}
NATIVE_DIRS = {"BravePipeExtractor", "PipePipeExtractor", "MediaServiceCore"}
NATIVE_SRC  = {".c", ".cpp", ".h", ".m", ".mm", ".s", ".S"}
MAX_SIZE    = 10 * 1024 * 1024   # 10 MB

def git_sha(raw: bytes) -> str:
    return hashlib.sha1(f"blob {len(raw)}\0".encode() + raw).hexdigest()

def api(method, url, **kw):
    """Call GitHub API, auto-sleep on rate limits, retry up to 10 times."""
    for attempt in range(10):
        try:
            r = getattr(SESSION, method)(url, timeout=30, **kw)
        except requests.exceptions.RequestException as e:
            print(f"  ⚠ Network error ({e}), retry {attempt+1}/10 …", flush=True)
            time.sleep(15 * (attempt + 1))
            continue
        if r.status_code in (429, 403) and any(k in r.text.lower() for k in ("rate", "secondary", "abuse")):
            wait = int(r.headers.get("Retry-After", 60))
            print(f"  ⏳ Rate limit — sleeping {wait}s then resuming automatically …", flush=True)
            time.sleep(wait)
            continue
        if r.status_code >= 500:
            print(f"  ⚠ GitHub 5xx ({r.status_code}), retry {attempt+1}/10 …", flush=True)
            time.sleep(20)
            continue
        return r
    raise RuntimeError(f"API permanently failed: {url}")

# ── Load checkpoint ─────────────────────────────────────────────────────────
checkpoint: dict = {}
if CKPT.exists():
    try:
        checkpoint = json.loads(CKPT.read_text())
        print(f"📂 Resumed from checkpoint ({len(checkpoint)} cached blobs)", flush=True)
    except Exception:
        pass

def save_checkpoint():
    CKPT.write_text(json.dumps(checkpoint))

# ── Get current main SHA ────────────────────────────────────────────────────
print("🔍 Getting main branch …", flush=True)
r = api("get", f"{BASE}/git/refs/heads/main")
main_sha = r.json()["object"]["sha"]
print(f"   main → {main_sha}", flush=True)

r = api("get", f"{BASE}/git/commits/{main_sha}")
base_tree_sha = r.json()["tree"]["sha"]
print(f"   tree → {base_tree_sha}", flush=True)

# ── Fetch existing GitHub tree ──────────────────────────────────────────────
print("📦 Fetching existing GitHub tree …", flush=True)
r = api("get", f"{BASE}/git/trees/{base_tree_sha}?recursive=1")
tree_data = r.json()
github_blobs = {i["path"]: i["sha"] for i in tree_data.get("tree", []) if i["type"] == "blob"}
truncated = tree_data.get("truncated", False)
print(f"   {len(github_blobs)} existing blobs  (truncated={truncated})", flush=True)

# Detect submodule entries (type=commit, mode=160000) — blobs under a submodule path
# cannot coexist with the submodule entry. We must delete submodule entries first.
# Note: only look at root-level tree entries (non-recursive) to find submodules.
r_root = api("get", f"{BASE}/git/trees/{base_tree_sha}")
submodule_prefixes = set()
for entry in r_root.json().get("tree", []):
    if entry.get("type") == "commit" and entry.get("mode") == "160000":
        submodule_prefixes.add(entry["path"])
        print(f"   ⚠ Submodule detected: {entry['path']} (will be replaced with flat files)", flush=True)

# ── Collect local files ─────────────────────────────────────────────────────
print("🗂  Collecting local files …", flush=True)
local_files = []
for path in sorted(ROOT.rglob("*")):
    if not path.is_file():
        continue
    rel   = path.relative_to(ROOT)
    parts = rel.parts
    if any(p in SKIP_DIRS for p in parts):
        continue
    if path.suffix in SKIP_EXT:
        continue
    if any(p in NATIVE_DIRS for p in parts) and path.suffix in NATIVE_SRC:
        continue
    if path.stat().st_size > MAX_SIZE:
        print(f"   ⚠ skip large: {rel}", flush=True)
        continue
    local_files.append((str(rel), path))
print(f"   {len(local_files)} files to sync", flush=True)

# ── Diff ────────────────────────────────────────────────────────────────────
# When using base_tree, GitHub keeps all files from the base tree unchanged.
# Only include files that DIFFER from the current GitHub tree in the tree call.
# IMPORTANT: Never trust stale checkpoint blob SHAs — GitHub GCs uncommitted blobs
# between sessions. Only github_blobs (confirmed live in the tree) can be skipped.
# Everything else MUST be re-uploaded to get a fresh, guaranteed-live blob SHA.
print("🔄 Diffing local vs GitHub + checkpoint …", flush=True)
need_upload = []
skip_count = 0
for rel, abs_path in local_files:
    try:
        raw = abs_path.read_bytes()
    except Exception as e:
        print(f"   ⚠ cannot read {rel}: {e}", flush=True)
        continue
    sha = git_sha(raw)
    if not truncated and github_blobs.get(rel) == sha:
        # Already correct in GitHub tree — omit from tree call (base_tree keeps it)
        skip_count += 1
        checkpoint[rel] = sha
    else:
        # Re-upload: either new/changed OR previously uploaded but GC'd since
        need_upload.append((rel, raw))

print(f"   ✅ skip   {skip_count:>5} (already correct in GitHub tree)", flush=True)
print(f"   📤 upload {len(need_upload):>5} blobs (new/changed/stale-checkpoint)", flush=True)

# ── Upload blobs (2 workers — conservative to avoid secondary rate limits) ──
uploaded = []
errors   = []
total    = len(need_upload)
done     = 0
t0       = time.time()

def make_blob(args):
    rel, raw = args
    try:
        try:    payload = {"content": raw.decode("utf-8"), "encoding": "utf-8"}
        except: payload = {"content": base64.b64encode(raw).decode(), "encoding": "base64"}
        r = api("post", f"{BASE}/git/blobs", json=payload)
        if r.status_code not in (200, 201):
            return None, rel, f"{r.status_code}: {r.text[:120]}"
        return r.json()["sha"], rel, None
    except Exception as e:
        return None, rel, str(e)

if total > 0:
    print(f"\n📤 Uploading {total} blobs (2 workers, auto-resumes after rate limits) …", flush=True)
    with ThreadPoolExecutor(max_workers=2) as ex:
        futs = {ex.submit(make_blob, item): item[0] for item in need_upload}
        for fut in as_completed(futs):
            sha, rel, err = fut.result()
            done += 1
            if err:
                errors.append((rel, err))
                print(f"   ✗ {rel}: {err}", flush=True)
            else:
                uploaded.append({"path": rel, "mode": "100644", "type": "blob", "sha": sha})
                checkpoint[rel] = sha           # save to checkpoint
            if done % 50 == 0 or done == total:
                save_checkpoint()               # persist progress
                el   = time.time() - t0
                rate = done / el if el else 0
                eta  = (total - done) / rate if rate else 0
                pct  = done * 100 // total
                print(f"   [{done:>5}/{total}] {pct}%  {rate:.1f} f/s  ETA {eta:.0f}s", flush=True)

    print(f"\n   ✅ uploaded {len(uploaded)}  errors {len(errors)}", flush=True)
    if errors:
        print("   Failed files:", flush=True)
        for rel, err in errors[:10]:
            print(f"      ✗ {rel}: {err}", flush=True)
else:
    print("   Nothing new to upload — all blobs already cached ✅", flush=True)

save_checkpoint()

# ── Build tree ──────────────────────────────────────────────────────────────
# Only include freshly-uploaded blobs (guaranteed to exist in GitHub's object store).
# Files already matching github_blobs are kept by base_tree automatically.
# Filter defensively — exclude any entry that somehow has no sha.
uploaded = [e for e in uploaded if e.get("sha")]

# Inject deletion entries for any submodule that our local files live under.
# A submodule (mode=160000) in base_tree blocks adding blobs under its path.
# Setting sha=null tells GitHub to delete that submodule entry from the tree.
submodule_deletions = []
for prefix in sorted(submodule_prefixes):
    # Check if any of our uploaded blobs live under this submodule prefix
    has_files_under = any(e["path"].startswith(prefix + "/") for e in uploaded)
    if has_files_under:
        submodule_deletions.append({
            "path": prefix,
            "mode": "160000",
            "type": "commit",
            "sha": None,
        })
        print(f"   🗑 Deleting submodule entry: {prefix}", flush=True)

all_entries = submodule_deletions + uploaded
print(f"\n🌲 Creating tree ({len(all_entries)} entries = {len(submodule_deletions)} deletions + {len(uploaded)} blobs, base {base_tree_sha[:8]}) …", flush=True)

def try_tree(base_sha, entries):
    """Attempt a tree creation, return (new_sha, None) on success or (None, err_msg) on failure."""
    for attempt in range(3):
        r = api("post", f"{BASE}/git/trees", json={"base_tree": base_sha, "tree": entries})
        if r.status_code in (200, 201):
            return r.json()["sha"], None
        if r.status_code == 422 and "BadObjectState" in r.text:
            wait = 10 * (attempt + 1)
            print(f"      BadObjectState attempt {attempt+1}/3 — retry {wait}s …", flush=True)
            time.sleep(wait)
            continue
        return None, f"{r.status_code} {r.text[:200]}"
    return None, "BadObjectState after 3 attempts"

# Strategy: always send submodule_deletions in the FIRST chunk so they
# arrive before any blob entries that live under the now-deleted submodule.
# Subsequent chunks are pure blob entries, chunked by CHUNK_SIZE.
CHUNK_SIZE = 30
current_tree_sha = base_tree_sha
skipped = []

# Build chunk list: first chunk = deletions + first N blobs, rest = blobs only
if submodule_deletions:
    first_blob_chunk = uploaded[:CHUNK_SIZE]
    rest_blobs = uploaded[CHUNK_SIZE:]
    chunks = [submodule_deletions + first_blob_chunk] + \
             [rest_blobs[i:i+CHUNK_SIZE] for i in range(0, len(rest_blobs), CHUNK_SIZE)]
else:
    chunks = [uploaded[i:i+CHUNK_SIZE] for i in range(0, len(uploaded), CHUNK_SIZE)]

print(f"   Chunking into {len(chunks)} groups (first has {len(chunks[0])} entries) …", flush=True)

for ci, chunk in enumerate(chunks):
    print(f"   Chunk {ci+1}/{len(chunks)} ({len(chunk)} entries) …", flush=True)
    new_sha, err = try_tree(current_tree_sha, chunk)
    if new_sha:
        current_tree_sha = new_sha
        continue
    # Chunk failed — bisect to find the bad entry (skip deletion entries when bisecting)
    print(f"   ⚠ Chunk {ci+1} failed ({err}), bisecting to find bad entry …", flush=True)
    good_entries = []
    for entry in chunk:
        if entry.get("sha") is None:
            # Deletion entry — always keep, it can't be the bad blob
            good_entries.append(entry)
            continue
        test_sha, test_err = try_tree(current_tree_sha, [entry])
        if test_sha:
            good_entries.append(entry)
        else:
            print(f"   ✗ Skipping bad entry: {entry['path']} (sha={entry['sha'][:8]})", flush=True)
            skipped.append(entry["path"])
    # Apply the good entries together
    if good_entries:
        new_sha2, err2 = try_tree(current_tree_sha, good_entries)
        if new_sha2:
            current_tree_sha = new_sha2
        else:
            print(f"   [ERROR] Good-entries retry also failed: {err2}")
            sys.exit(1)

new_tree_sha = current_tree_sha
if skipped:
    print(f"   ⚠ Skipped {len(skipped)} bad entries: {skipped[:5]}", flush=True)
print(f"   tree → {new_tree_sha}", flush=True)

# ── Create commit ────────────────────────────────────────────────────────────
print("💾 Creating commit …", flush=True)
r = api("post", f"{BASE}/git/commits", json={
    "message": (
        "chore: full NasTech rebrand + embed all deps (core, libs)\n\n"
        "- Rebrand SimpMusic → NasMusic (7800+ files, zero violations)\n"
        "- Android package ID: com.nastechai.nasmusic\n"
        "- URLs → nastechai.com | GitHub org → nastech-ai\n"
        "- 14 NasTech libs embedded in libs/ (all rebranded)\n"
        "- core/ embedded (common, data, domain)\n"
        "- GitHub Actions workflow + self-hosted runner configured"
    ),
    "tree": new_tree_sha,
    "parents": [main_sha],
})
if r.status_code not in (200, 201):
    print(f"[ERROR] Commit: {r.status_code} {r.text[:400]}")
    sys.exit(1)
new_commit_sha = r.json()["sha"]
print(f"   commit → {new_commit_sha}", flush=True)

# ── Update main ref ──────────────────────────────────────────────────────────
print("🚀 Pushing to main …", flush=True)
r = api("patch", f"{BASE}/git/refs/heads/main", json={"sha": new_commit_sha, "force": True})
if r.status_code not in (200, 201):
    print(f"[ERROR] Ref: {r.status_code} {r.text[:400]}")
    sys.exit(1)

# Clean up checkpoint on success
CKPT.unlink(missing_ok=True)

elapsed = time.time() - t0
print(f"\n{'='*60}", flush=True)
print(f"✅  PUSHED  →  github.com/{OWNER}/{REPO}", flush=True)
print(f"   Commit  : {new_commit_sha}", flush=True)
print(f"   Files   : {len(all_entries)} synced  ({len(uploaded)} uploaded)", flush=True)
print(f"   Time    : {elapsed:.0f}s", flush=True)
print(f"   Actions : https://github.com/{OWNER}/{REPO}/actions", flush=True)
print("="*60, flush=True)
