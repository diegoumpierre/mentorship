# Streams: Functional Building Blocks

| Activities |
|------------|
| - [ ] Use a `Supplier` to feed `Stream.generate`, then cap it with `limit` to grab a sample |
| - [ ] Drop a `Consumer` into `peek` for ad-hoc debugging |
| - [ ] Use a `Consumer` as the terminal `forEach`, then chain two of them with `andThen` |
| - [ ] Reduce a list of numbers with a `BinaryOperator` and an identity |
| - [ ] Try the `BinaryOperator.minBy` / `maxBy` helpers on a custom comparator |
| - [ ] Use `Collectors.reducing` so each group reduces independently |
| - [ ] Use a `UnaryOperator` to feed `Stream.iterate` (Fibonacci is a classic) |
| - [ ] Use `List.replaceAll` with a `UnaryOperator` to mutate a list in place |
| - [ ] Wire a tiny pipeline that uses all four of these in one go |
| - [ ] Show what goes wrong if you mutate shared state from inside a `parallelStream` |
