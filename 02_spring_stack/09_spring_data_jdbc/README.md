# Spring Data JDBC

| Activities |
|------------|
| - [ ] Pull in `spring-boot-starter-data-jdbc` and point the app at a real database (Postgres via Testcontainers, or H2 for quick spikes) |
| - [ ] Model an aggregate as a Java record annotated with `@Table` and `@Id` |
| - [ ] Write a `CrudRepository<T, ID>` and exercise `save`, `findById`, `findAll`, `deleteById` |
| - [ ] Add a derived query method (`findByEmail`, `findByStatusAndCreatedAtAfter`) and let Spring Data generate the SQL |
| - [ ] Drop down to a hand-written query with `@Query` when the derived form gets ugly |
| - [ ] Model a one-to-many with embedded child entities and see how Spring Data JDBC saves and loads the whole aggregate as a unit |
| - [ ] Create a Flyway (or Liquibase) migration script and let the schema be managed by it instead of `ddl-auto` |
| - [ ] Add an `@Version` field for optimistic locking and provoke an `OptimisticLockingFailureException` |
| - [ ] Listen to lifecycle events with `BeforeSaveCallback` / `AfterSaveCallback` |
| - [ ] Compare Spring Data JDBC and Spring Data JPA on one of the same operations: which queries actually fire, and which abstractions you give up |
| - [ ] Write an integration test against a Testcontainers Postgres so the SQL is real |
