# Quarkus

| Activities |
|------------|
| - [ ] Bootstrap a project with the Quarkus CLI (or `code.quarkus.io`) and run it in dev mode — watch live reload |
| - [ ] Write a JAX-RS endpoint with `@Path` / `@GET` / `@POST` and bind path/query/body parameters |
| - [ ] Inject a CDI bean with `@Inject` and pin its scope (`@ApplicationScoped`, `@RequestScoped`) |
| - [ ] Read configuration with `@ConfigProperty(name = "...")` and put environment-specific values in `application.properties` (`%dev`, `%prod`) |
| - [ ] Use Quarkus REST Client (`@RegisterRestClient`) to call out to another HTTP service |
| - [ ] Persist with Hibernate ORM with Panache — both the active-record style and the repository style |
| - [ ] Plug in Flyway and let it own the schema |
| - [ ] Toggle to the reactive stack: Mutiny `Uni` / `Multi` endpoints, reactive Panache, reactive REST client |
| - [ ] Wire health, metrics and OpenTelemetry through SmallRye / Micrometer |
| - [ ] Secure endpoints with the OIDC extension and `@RolesAllowed` |
| - [ ] Write tests with `@QuarkusTest`; mock a CDI bean with `@InjectMock` |
| - [ ] Run an integration test with `@QuarkusIntegrationTest` against the packaged artifact |
| - [ ] Build a JVM container image (`quarkus build -Dquarkus.container-image.build=true`), then a native image with GraalVM, and compare startup time and RSS |
| - [ ] Compare the same endpoint side-by-side with Spring Boot — note the differences in startup, memory, and ergonomics |
