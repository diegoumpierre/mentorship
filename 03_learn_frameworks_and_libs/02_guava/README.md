# Guava

| Activities |
|------------|
| - [ ] Build immutable collections with `ImmutableList`, `ImmutableSet`, `ImmutableMap` and notice they fail fast on mutation |
| - [ ] Replace nested `Map<K, Map<K, V>>` with `Multimap` (`ArrayListMultimap`, `HashMultimap`) |
| - [ ] Use `BiMap` when you need lookup by both key and value |
| - [ ] Reach for `Table<R, C, V>` for the two-key lookup case |
| - [ ] Try `RangeSet` and `RangeMap` for interval logic that's painful with vanilla collections |
| - [ ] Build a small in-process cache with `CacheBuilder` (TTL, max size, eviction listener, `recordStats()`) |
| - [ ] Wire a `LoadingCache` so misses populate themselves |
| - [ ] Use `EventBus` for in-process pub/sub on a tiny example |
| - [ ] Hash a payload with `Hashing.sha256()` and verify equality |
| - [ ] Write a quick `BloomFilter` and measure the false-positive rate |
| - [ ] Use `Splitter` and `Joiner` instead of `String.split` / `String.join` for the trim/limit/skip-empty edge cases |
| - [ ] Use `Preconditions.checkArgument` / `checkNotNull` / `checkState` to fail loud at the boundary |
| - [ ] Use `Stopwatch` for ad-hoc timing in a quick benchmark |
| - [ ] Flag deprecated APIs with `@Beta` and explain what that label means |
