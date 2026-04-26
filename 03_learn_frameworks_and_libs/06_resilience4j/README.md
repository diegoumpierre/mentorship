# Resilience4j

| Activities |
|------------|
| - [ ] Pick a flaky downstream call and wrap it with a `CircuitBreaker` — tune `failureRateThreshold`, `slidingWindow`, `waitDurationInOpenState` |
| - [ ] Watch the breaker move through CLOSED → OPEN → HALF_OPEN under load |
| - [ ] Add a `Retry` with exponential backoff and an explicit list of retryable exceptions |
| - [ ] Stack `Retry` and `CircuitBreaker` on the same call and reason about the order they fire in |
| - [ ] Limit concurrency with a `Bulkhead` (semaphore) and then with a `ThreadPoolBulkhead` |
| - [ ] Throttle requests with a `RateLimiter` and provoke a `RequestNotPermitted` |
| - [ ] Set a hard deadline with `TimeLimiter` (works on a `CompletableFuture`) |
| - [ ] Wire all of the above declaratively via the Spring Boot starter (`@CircuitBreaker(name = ...)`, etc.) and also programmatically with the registries |
| - [ ] Provide a `fallbackMethod` and verify it runs on open / on exception |
| - [ ] Subscribe to events on the `EventPublisher` (`onError`, `onSuccess`, `onStateTransition`) for logging |
| - [ ] Expose Resilience4j metrics through Micrometer and look at the values in `/actuator/prometheus` |
| - [ ] Write a unit test that forces failures and asserts the breaker opens at the expected threshold |
