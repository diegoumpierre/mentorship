# DontPad Clone

| Activities |
|------------|
| - [ ] Model the domain: `Pad` (id from the URL slug, content, updatedAt, version) — no users, no auth, that's the whole point |
| - [ ] One endpoint per verb: `GET /:slug` returns the content, `PUT /:slug` saves it |
| - [ ] Auto-create a pad on first read with empty content — the URL is the namespace |
| - [ ] Push live updates to other viewers: WebSocket (or SSE) per pad room |
| - [ ] Send only diffs over the wire, not the whole text — start with a simple "replace from offset N" patch |
| - [ ] Concurrency: two writers on the same pad — start with last-write-wins + version check, then explore OT/CRDT if the appetite is there |
| - [ ] Debounce client saves (e.g., 500 ms idle) so every keystroke isn't a `PUT` |
| - [ ] Persist to disk or a small DB; pads are small and many — single file per pad is fine for v1 |
| - [ ] Set a reasonable max size and reject silently-large pastes with a `413` |
| - [ ] Rate-limit by IP per pad to keep abuse out |
| - [ ] Slug rules: no path traversal (`..`), no leading slash, length cap, allowed character set |
| - [ ] Tests for: create-on-first-read, two clients see each other's edits live, version conflict handling, oversized payload rejection |
| - [ ] Optional: passwordless protection by adding `?key=...` checked against a hash stored with the pad |
