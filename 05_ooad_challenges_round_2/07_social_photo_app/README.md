# Social Photo App

| Activities |
|------------|
| - [ ] Model the domain: `User`, `Photo`, `Tag`, `Comment`, `Like`, `Follow`, `Timeline` |
| - [ ] Separate the photo bytes (object store) from the metadata (db) — they have different scaling needs |
| - [ ] Generate thumbnails on upload, asynchronously; the publish API should return as soon as bytes land |
| - [ ] Tag photos by user mention (`@alice`) and by topic (`#sunset`) — both are first-class but live in different tables |
| - [ ] Authorize tag visibility: a user can untag themselves; the photo owner can remove tags |
| - [ ] Build the timeline two ways and write down the trade-off: pull (query at read time) vs push (fan-out on publish) |
| - [ ] For the push model, fan out via an event bus — and skip fan-out for celebrity accounts (hybrid) |
| - [ ] Page the timeline by cursor, never by offset (offsets break with concurrent inserts) |
| - [ ] Comments and likes are append-only; show counts from a separate counter, not by counting rows on every read |
| - [ ] Soft-delete photos and cascade to comments and likes |
| - [ ] Privacy: public / followers-only / private — apply the filter at the read boundary, not in the UI |
| - [ ] Emit events on every social action (publish, like, comment, follow) for downstream features (notifications, analytics) |
| - [ ] Tests for: tag a non-follower (allowed/denied per privacy rule), timeline ordering across multiple authors, cursor stability under concurrent publishes |
