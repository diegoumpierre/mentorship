# Vert.x

| Activities |
|------------|
| - [ ] Stand up a `Vertx` instance and deploy a `AbstractVerticle` with `vertx.deployVerticle(...)` |
| - [ ] Write an HTTP server with `vertx.createHttpServer().requestHandler(router::handle)` and a couple of routes |
| - [ ] Bind path params, query params, JSON body via `BodyHandler` |
| - [ ] Make sure you never block the event loop (try a `Thread.sleep` and watch the warning) — push slow work onto `executeBlocking` |
| - [ ] Send a message between verticles over the `EventBus` (point-to-point and pub/sub) |
| - [ ] Move from callbacks to `Future` composition (`compose`, `onSuccess`, `onFailure`, `recover`) |
| - [ ] Try the RxJava 3 bindings and the Mutiny bindings — pick the flavor that reads cleanest to you |
| - [ ] Call out with `WebClient` and combine several async calls into one response |
| - [ ] Use `ConfigRetriever` to load config from files, env vars, system properties |
| - [ ] Persist with the reactive Postgres client (`io.vertx.pgclient`) |
| - [ ] Deploy multiple verticle instances (`new DeploymentOptions().setInstances(...)`) and watch the load spread across event loops |
| - [ ] Add a circuit breaker via `vertx-circuit-breaker` |
| - [ ] Test with `vertx-junit5` and `VertxTestContext` |
| - [ ] Compare the Vert.x server with a Spring WebFlux server on the same workload — pay attention to memory and tail latency |
