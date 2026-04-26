# Spring Core - Setter/Constructor Injection

| Activities |
|------------|
| - [ ] Inject a dependency through the constructor — the recommended default — and drop the explicit `@Autowired` since it's optional on a single constructor |
| - [ ] Inject the same dependency through a setter and write down which use cases actually justify it (optional collaborators, circular wiring) |
| - [ ] Inject through a field with `@Autowired` and note why it makes testing harder |
| - [ ] Use `final` on constructor-injected fields and let Lombok's `@RequiredArgsConstructor` cut the boilerplate |
| - [ ] Inject a `List<Strategy>` and a `Map<String, Strategy>` and watch Spring fill them with every matching bean |
| - [ ] Mark an optional dependency with `@Autowired(required = false)` and show what arrives when no bean exists |
| - [ ] Inject an `Optional<T>` or a `java.util.Provider<T>` for the same effect, more idiomatically |
| - [ ] Resolve an ambiguity with `@Qualifier` on the constructor parameter |
| - [ ] Reproduce a circular dependency between two singletons and break the cycle with setter injection or `@Lazy` |
| - [ ] Write a unit test for a constructor-injected service without bringing the Spring context up |
