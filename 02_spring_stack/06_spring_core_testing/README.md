# Spring Core - Testing, Environment, WebTestClient

| Activities |
|------------|
| - [ ] Spin up a context in a test with `@SpringJUnitConfig` (or `@ContextConfiguration` + `SpringExtension`) and pull a bean out |
| - [ ] Swap a real bean for a fake using `@MockBean` (or `@TestConfiguration` for a hand-rolled stub) |
| - [ ] Activate a profile in a test with `@ActiveProfiles("test")` and load a profile-specific properties file |
| - [ ] Override properties for a single test class with `@TestPropertySource` |
| - [ ] Cache the context across tests on purpose — and break the cache on purpose with `@DirtiesContext` to see the impact |
| - [ ] Inspect the `Environment` directly: list active profiles, fetch a property, walk every `PropertySource` |
| - [ ] Add a custom `PropertySource` programmatically and watch it show up in the order |
| - [ ] Bring up a slice of the app with `@WebFluxTest` and hit it with `WebTestClient` |
| - [ ] Use `WebTestClient.bindToServer()` against a real running app on a random port |
| - [ ] Assert response status, headers and JSON body with `expectStatus`, `expectHeader`, `expectBody().jsonPath(...)` |
| - [ ] Send a typed body with `bodyValue(...)` and consume it back with `expectBody(MyDto.class)` |
| - [ ] Mock the auth context for a secured endpoint test |
