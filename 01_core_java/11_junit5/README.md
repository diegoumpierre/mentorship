# JUnit 5

| Activities |
|------------|
| - [ ] Write a basic `@Test` and exercise the most common assertions (`assertEquals`, `assertTrue`, `assertNull`) |
| - [ ] Group related checks under a single `assertAll` so one failure doesn't hide the others |
| - [ ] Cover the lifecycle: `@BeforeAll`, `@AfterAll`, `@BeforeEach`, `@AfterEach` |
| - [ ] Switch the test instance lifecycle to `PER_CLASS` and notice that `@BeforeAll` no longer needs to be static |
| - [ ] Make output readable with `@DisplayName` and group cases under `@Nested` |
| - [ ] Parameterize tests with `@ValueSource`, `@CsvSource`, `@CsvFileSource` |
| - [ ] Pull arguments from a method via `@MethodSource`, from an enum via `@EnumSource`, and from your own `ArgumentsProvider` |
| - [ ] Run a flaky case under `@RepeatedTest` |
| - [ ] Generate cases at runtime with `@TestFactory` |
| - [ ] Tag tests with `@Tag` and filter by tag from the build tool |
| - [ ] Use `assumeTrue` / `assumeFalse` to skip when preconditions don't hold; use `assumingThat` for partial execution |
| - [ ] Bound a slow test with `assertTimeout` (and the preemptive variant) |
| - [ ] Assert exceptions with `assertThrows` and inspect the thrown object |
| - [ ] Build a custom extension by implementing `BeforeEachCallback`/`AfterEachCallback`, then a `ParameterResolver` |
| - [ ] Force an order with `@TestMethodOrder` + `OrderAnnotation` (and write down why you usually shouldn't) |
| - [ ] Skip on the wrong OS with `@EnabledOnOs` / `@DisabledOnOs`; gate on a custom condition with `@EnabledIf` |
