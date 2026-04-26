# Cache with Caffeine

| Activities |
|------------|
| - [ ] Add Spring's caching abstraction with `@EnableCaching` and the Caffeine starter |
| - [ ] Configure a `CaffeineCacheManager` bean — set the spec via properties (`spring.cache.caffeine.spec`) |
| - [ ] Put `@Cacheable` on a slow service method and prove the second call short-circuits |
| - [ ] Customize the cache key with SpEL (`key = "#user.id"`) and a `keyGenerator` for the harder cases |
| - [ ] Use `condition` and `unless` to skip caching for some inputs/outputs |
| - [ ] Bust an entry with `@CacheEvict`, and clear the whole cache with `allEntries = true` |
| - [ ] Update an entry inline with `@CachePut` |
| - [ ] Apply several caching annotations to one method via `@Caching` |
| - [ ] Configure two named caches with different specs (TTL, max size) and route methods to each |
| - [ ] Wire in a `CacheLoader` for the loading-cache pattern (compute on miss inside the cache) |
| - [ ] Read out cache hit/miss/eviction stats from `Caffeine.recordStats()` and expose them through Micrometer |
| - [ ] Stress the cache with concurrent reads to make sure the protected method is called only once per key |
