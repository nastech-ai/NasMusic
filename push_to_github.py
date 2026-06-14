#!/usr/bin/env python3
"""Push NasMusic source files to nastech-ai/NasMusic via GitHub API."""

import os, sys, json, base64, hashlib, time, requests
from pathlib import Path
from concurrent.futures import ThreadPoolExecutor, as_completed

TOKEN  = os.environ["GITHUB_PERSONAL_ACCESS_TOKEN"]
OWNER  = "nastech-ai"
REPO   = "NasMusic"
BASE   = f"https://api.github.com/repos/{OWNER}/{REPO}"
ROOT   = Path("/home/runner/workspace")

SESSION = requests.Session()
SESSION.headers.update({
    "Authorization": f"token {TOKEN}",
    "Accept": "application/vnd.github.v3+json",
    "Content-Type": "application/json",
})

# ── Skip these entirely — native/binary repos not needed for Kotlin/Gradle build
SKIP_DIRS = {
    ".git", "build", ".gradle", "actions-runner", "__pycache__",
    ".local", "captures", ".idea", "intermediates", "generated",
    # Large native/non-Kotlin repos — not needed for Android APK build
    "ffmpeg-kit-16KB",   # 2577 C/header files — included via AAR in Gradle
    "rococoa",           # Objective-C macOS bridge — desktop only, native
    "android-youtubeExtractor",  # archived Java lib
    "lyricsplus",        # JavaScript serverless — not an Android module
}
# Skip by file extension
SKIP_EXT = {
    ".class", ".pyc", ".pyo",
    ".so", ".a", ".o", ".dll", ".lib", ".dylib",  # native binaries
    ".exe", ".bin",
}
# Skip source extensions inside native-heavy dirs (C/C++ in extractors etc.)
SKIP_SOURCE_EXT_IN_NATIVE = {".c", ".cpp", ".h", ".m", ".mm", ".s", ".S"}
NATIVE_DIRS = {"BravePipeExtractor", "PipePipeExtractor", "MediaServiceCore"}

MAX_SIZE = 10 * 1024 * 1024  # 10 MB

def git_sha(raw: bytes) -> str:
    h = hashlib.sha1(f"blob {len(raw)}\0".encode() + raw)
    return h.hexdigest()

def api(method, url, **kw):
    for attempt in range(6):
        r = getattr(SESSION, method)(url, **kw)
        if r.status_code in (429, 403) and ("rate" in r.text.lower() or "secondary" in r.text.lower()):
            wait = int(r.headers.get("Retry-After", 30))
            print(f"  ⏳ Rate limit — sleeping {wait}s …", flush=True)
            time.sleep(wait)
            continue
        return r
    raise RuntimeError(f"API failed after retries: {url}")

# ── 1. Get current main SHA ─────────────────────────────────────────────────
print("🔍 Getting main branch …", flush=True)
r = api("get", f"{BASE}/git/refs/heads/main")
main_sha = r.json()["object"]["sha"]
print(f"   main → {main_sha}", flush=True)

# ── 2. Get base tree SHA ────────────────────────────────────────────────────
r = api("get", f"{BASE}/git/commits/{main_sha}")
base_tree_sha = r.json()["tree"]["sha"]
print(f"   tree → {base_tree_sha}", flush=True)

# ── 3. Fetch existing GitHub tree ───────────────────────────────────────────
print("📦 Fetching existing GitHub tree …", flush=True)
r = api("get", f"{BASE}/git/trees/{base_tree_sha}?recursive=1")
github_blobs = {
    item["path"]: item["sha"]
    for item in r.json().get("tree", [])
    if item["type"] == "blob"
}
truncated = r.json().get("truncated", False)
print(f"   {len(github_blobs)} existing blobs on GitHub  (truncated={truncated})", flush=True)

# ── 4. Collect local files ──────────────────────────────────────────────────
print("🗂  Collecting local files …", flush=True)
local_files = []
for path in sorted(ROOT.rglob("*")):
    if not path.is_file():
        continue
    rel  = path.relative_to(ROOT)
    parts = rel.parts

    # Skip dirs
    if any(p in SKIP_DIRS for p in parts):
        continue
    # Skip by extension
    if path.suffix in SKIP_EXT:
        continue
    # Skip C/native source inside native-heavy lib dirs
    if any(p in NATIVE_DIRS for p in parts) and path.suffix in SKIP_SOURCE_EXT_IN_NATIVE:
        continue
    # Skip oversized files
    if path.stat().st_size > MAX_SIZE:
        print(f"   ⚠ skip large: {rel}", flush=True)
        continue
    local_files.append((str(rel), path))

print(f"   {len(local_files)} files to sync", flush=True)

# ── 5. Diff — only upload what changed ─────────────────────────────────────
print("🔄 Diffing …", flush=True)
reuse, upload = [], []
for rel, abs_path in local_files:
    try:
        raw = abs_path.read_bytes()
    except Exception as e:
        print(f"   ⚠ cannot read {rel}: {e}", flush=True)
        continue
    sha = git_sha(raw)
    if not truncated and github_blobs.get(rel) == sha:
        reuse.append({"path": rel, "mode": "100644", "type": "blob", "sha": sha})
    else:
        upload.append((rel, raw))

print(f"   ✅ reuse  {len(reuse):>5} unchanged blobs", flush=True)
print(f"   📤 upload {len(upload):>5} new/changed blobs", flush=True)

# ── 6. Upload blobs (4 workers, rate-limit aware) ───────────────────────────
uploaded = []
errors   = []

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

total = len(upload)
done  = 0
t0    = time.time()
print(f"\n📤 Uploading {total} blobs (4 workers) …", flush=True)

with ThreadPoolExecutor(max_workers=4) as ex:
    futs = {ex.submit(make_blob, item): item[0] for item in upload}
    for fut in as_completed(futs):
        sha, rel, err = fut.result()
        done += 1
        if err:
            errors.append((rel, err))
            print(f"   ✗ {rel}: {err}", flush=True)
        else:
            uploaded.append({"path": rel, "mode": "100644", "type": "blob", "sha": sha})
        if done % 100 == 0 or done == total:
            el  = time.time() - t0
            rate = done / el if el else 0
            eta  = (total - done) / rate if rate else 0
            print(f"   [{done:>5}/{total}] {rate:.1f} f/s  ETA {eta:.0f}s", flush=True)

print(f"\n   ✅ uploaded {len(uploaded)}  errors {len(errors)}", flush=True)
if errors:
    for rel, err in errors[:10]:
        print(f"      ✗ {rel}: {err}", flush=True)

# ── 7. Create new tree ──────────────────────────────────────────────────────
all_entries = reuse + uploaded
print(f"\n🌲 Creating tree ({len(all_entries)} entries) …", flush=True)
r = api("post", f"{BASE}/git/trees", json={
    "base_tree": base_tree_sha,
    "tree": all_entries,
})
if r.status_code not in (200, 201):
    print(f"[ERROR] Tree failed: {r.status_code} {r.text[:400]}")
    sys.exit(1)
new_tree_sha = r.json()["sha"]
print(f"   new tree → {new_tree_sha}", flush=True)

# ── 8. Create commit ────────────────────────────────────────────────────────
print("💾 Creating commit …", flush=True)
msg = (
    "chore: full NasTech rebrand + embed all deps (core, libs)\n\n"
    "- Rebrand SimpMusic → NasMusic (7800+ files, zero violations)\n"
    "- Android package ID: com.nastechai.nasmusic\n"
    "- URLs → nastechai.com | GitHub → nastech-ai\n"
    "- 14 NasTech libs embedded in libs/ (all rebranded)\n"
    "- core/ embedded (common, data, domain)\n"
    "- GitHub Actions workflow + self-hosted runner added"
)
r = api("post", f"{BASE}/git/commits", json={
    "message": msg,
    "tree": new_tree_sha,
    "parents": [main_sha],
})
if r.status_code not in (200, 201):
    print(f"[ERROR] Commit failed: {r.status_code} {r.text[:400]}")
    sys.exit(1)
new_commit_sha = r.json()["sha"]
print(f"   commit → {new_commit_sha}", flush=True)

# ── 9. Update main ref ──────────────────────────────────────────────────────
print("🚀 Pushing to main …", flush=True)
r = api("patch", f"{BASE}/git/refs/heads/main", json={
    "sha": new_commit_sha,
    "force": True,
})
if r.status_code not in (200, 201):
    print(f"[ERROR] Ref update failed: {r.status_code} {r.text[:400]}")
    sys.exit(1)

elapsed = time.time() - t0
print(f"\n{'='*60}", flush=True)
print(f"✅  PUSHED to github.com/{OWNER}/{REPO}", flush=True)
print(f"   Commit : {new_commit_sha}", flush=True)
print(f"   Files  : {len(all_entries)} synced  ({len(uploaded)} uploaded)", flush=True)
print(f"   Time   : {elapsed:.0f}s", flush=True)
print(f"   Actions: https://github.com/{OWNER}/{REPO}/actions", flush=True)
print("="*60, flush=True)
