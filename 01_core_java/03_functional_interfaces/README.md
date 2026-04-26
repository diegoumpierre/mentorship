# Functional Interfaces

| Activities |
|------------|
| - [ ] Write your own `@FunctionalInterface` and prove the single-abstract-method rule by adding a second abstract method (compiler should yell) |
| - [ ] Throw in a default method and a static factory method on top of it |
| - [ ] Use `Function`, `Predicate`, `Consumer` and `Supplier` against a small domain (say, a `Customer` record) |
| - [ ] Reach for the bi-variants where it actually fits — `BiFunction`, `BiPredicate`, `BiConsumer` |
| - [ ] Pick a primitive specialization (`IntFunction`, `ToIntFunction`, `IntPredicate`) and explain to yourself why boxing matters here |
| - [ ] Glue functions together with `andThen` and `compose`, and show the order isn't the same |
| - [ ] Combine predicates with `and`, `or`, `negate` to express a non-trivial filter |
| - [ ] Take one snippet and rewrite it three ways: lambda, method reference, anonymous class |
| - [ ] Cover the four flavors of method reference: static, bound, unbound, constructor |
| - [ ] Capture an effectively-final local from a lambda; then try to mutate it and watch the compiler complain |
| - [ ] Pass one of these interfaces as a parameter, then return one — both directions |
