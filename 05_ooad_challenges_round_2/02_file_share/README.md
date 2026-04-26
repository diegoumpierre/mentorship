# File Share

| Activities |
|------------|
| - [ ] Model the domain: `StoredFile` (id, name, size, owner, createdAt, checksum), `FileVersion`, `Tag`, `Permission` |
| - [ ] Define a `FileStore` interface and back it with at least two implementations: local filesystem and an in-memory one for tests |
| - [ ] Stream uploads and downloads through `InputStream` / `OutputStream` — never load the whole file in memory |
| - [ ] Compute a checksum (SHA-256) on save and verify it on restore |
| - [ ] Encryption: `EncryptionService` interface, AES-GCM as the default, never roll your own crypto |
| - [ ] Manage keys deliberately: per-user master key, derived per-file data key (envelope encryption) |
| - [ ] Store the IV/nonce alongside the ciphertext (never reuse a nonce) |
| - [ ] Soft-delete by default with a `restore` operation; hard-delete only after a retention window |
| - [ ] Index file metadata for `listFiles` and `search` (by name, owner, tag, date range) — keep the index decoupled from the bytes |
| - [ ] Authorize every operation against the file's permissions, even `listFiles` (filter the result) |
| - [ ] Concurrency: two writers on the same file — pick optimistic versioning and reject on conflict |
| - [ ] Audit trail: every save / restore / delete writes an event with who, what, when |
| - [ ] Tests for: upload/download roundtrip, encryption tamper detection, search filters, restore after soft-delete, permission denied |
