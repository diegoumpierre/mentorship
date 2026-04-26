# Redis Clone (Client/Server)

| Activities |
|------------|
| - [ ] Pick a wire protocol: a tiny RESP-like format is the right call — easy to debug with `nc` |
| - [ ] Build the server on plain TCP (NIO or Netty/Vert.x); one event loop, no per-connection thread |
| - [ ] Implement a `CommandHandler` per command (`SET`, `GET`, `DEL`, `APPEND`, `HSET`, `HGET`, `HKEYS`, `HVALS`) — Command pattern, no giant `switch` |
| - [ ] Back it with a single `KeyValueStore` interface; first impl: in-memory `ConcurrentHashMap` |
| - [ ] Type-tag values (`String` vs `Hash`) and reject mismatched ops with a clean error (Redis returns WRONGTYPE) |
| - [ ] Add expiration: optional `TTL` per key, lazy eviction on access plus a periodic sweeper |
| - [ ] Build the client as a thin wrapper that pipelines commands and reads replies in order |
| - [ ] Make the client connection-pooled; the server should handle thousands of concurrent connections |
| - [ ] Persist: append-only log of mutations, replayed on startup (the AOF idea) |
| - [ ] Snapshot the store periodically and truncate the log past the snapshot |
| - [ ] Tests at three levels: store unit tests, command parser, end-to-end client→server with assertions |
| - [ ] Benchmark a tight loop of `SET`/`GET` and write down the throughput — useful baseline if you optimize later |
