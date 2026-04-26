# Spring Boot - Integration Test with Rest

| Activities |
|------------|
| - [ ] Bring the whole app up with `@SpringBootTest(webEnvironment = RANDOM_PORT)` |
| - [ ] Hit the running app with `TestRestTemplate`, then redo the same test with `WebTestClient` for comparison |
| - [ ] Slice it down with `@WebMvcTest(SomeController.class)` when only the web layer is under test, using `MockMvc` |
| - [ ] Cover happy paths and validation failures (400) and not-found cases (404) |
| - [ ] Stub out collaborators with `@MockBean` so the test stays focused on the controller |
| - [ ] Hook a real database into the test with Testcontainers (Postgres is the canonical example) |
| - [ ] Reset state between tests cleanly — `@Sql` scripts, `@Transactional`, or container-per-class |
| - [ ] Stub a downstream HTTP call with WireMock or `MockRestServiceServer` |
| - [ ] Assert response bodies with JSONPath via `MockMvc` and with `expectBody().jsonPath(...)` via `WebTestClient` |
| - [ ] Test a secured endpoint with `@WithMockUser` (or `mutateWith(mockUser(...))` for `WebTestClient`) |
| - [ ] Pin a slow test under a deadline with `assertTimeoutPreemptively` |
| - [ ] Keep test startup snappy by letting Spring cache the context across the whole class |
