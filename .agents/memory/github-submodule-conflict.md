---
name: GitHub submodule path conflict
description: When a base tree has a submodule entry, blobs under that path fail with BadObjectState unless you delete the submodule first.
---

## Rule
If the GitHub base tree contains a submodule entry (mode=160000, type=commit) at a path like `core/`, any attempt to add blob entries under `core/LICENSE`, `core/common/...` etc. via `/git/trees` will fail with `GitRPC::BadObjectState` — even with fresh, verified blob SHAs.

**Why:** The submodule entry in the base tree occupies the `core` name as a "commit" type. Git doesn't allow blob paths that descend from a commit-type entry.

**How to apply:** When building a tree with `base_tree`, detect submodule entries in the root-level tree (not recursive). For any submodule whose path is a prefix of files you want to add, include `{"path": "core", "mode": "160000", "type": "commit", "sha": null}` as a deletion entry — and put it in the FIRST chunk, before any blob entries under that path.

Detection: `GET /git/trees/{sha}` (non-recursive), filter `type == "commit"`.
