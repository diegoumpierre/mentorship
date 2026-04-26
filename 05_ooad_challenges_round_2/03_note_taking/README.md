# Note Taking

| Activities |
|------------|
| - [ ] Model the domain: `Note` (id, title, body, createdAt, updatedAt, tags, ownerId) and `Notebook` |
| - [ ] Implement the basics: add, save (with autosave debounced), edit, delete (soft-delete first) |
| - [ ] Keep an edit history per note so older versions can be restored |
| - [ ] Decide what `save` means relative to `edit` — explicit checkpoint vs continuous autosave — and pick one |
| - [ ] Sync: model it as bidirectional, not just upload — the server might have changes the client doesn't have yet |
| - [ ] Track sync state per note (`pending`, `synced`, `conflict`) and a per-note version vector or `updatedAt` |
| - [ ] Resolve conflicts deliberately: last-write-wins for the demo, but design the seam so a CRDT/merge resolver could replace it |
| - [ ] Make the client work offline: enqueue mutations, drain the queue on reconnect |
| - [ ] Search across notes by title, body, tag — keep the search behind a `NoteSearch` interface |
| - [ ] Encrypt note bodies at rest if the spec calls for privacy (key per user) |
| - [ ] Emit events on every change so collaborators can subscribe |
| - [ ] Tests for: edit-while-offline + sync, conflicting edits from two devices, restore from history, soft-delete + restore |
