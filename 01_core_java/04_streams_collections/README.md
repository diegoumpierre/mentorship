# Streams: Collections

| Activities |
|------------|
| - [ ] Open a stream from a `List`, a `Set` and a `Map.entrySet` |
| - [ ] Open one from an array via `Arrays.stream` and another from `Stream.of` |
| - [ ] Filter with a `Predicate`, then map values, then chain a `flatMap` over a nested collection |
| - [ ] Add `distinct`, `sorted`, `limit`, `skip` and look at how order matters |
| - [ ] Finish pipelines with `forEach`, `count`, `min`, `max` |
| - [ ] Reduce with both forms: identity + accumulator, and the no-identity overload that returns `Optional` |
| - [ ] Try every short-circuit terminal: `findFirst`, `findAny`, `anyMatch`, `allMatch`, `noneMatch` |
| - [ ] Collect into a `List`, a `Set`, a `Map`, then redo it with the unmodifiable variants |
| - [ ] Build a CSV-ish string with `Collectors.joining` |
| - [ ] Group a list of objects by a field with `groupingBy` and add a downstream collector |
| - [ ] Split with `partitioningBy` |
| - [ ] Use `counting`, `summarizingInt`, `averagingDouble` to get stats per group |
| - [ ] Combine three predicates with `and`/`or`/`negate` for a filter that would be ugly as one big lambda |
| - [ ] Iterate a `Map`'s `entrySet`, `keySet` and `values` as streams |
| - [ ] Run the same heavy computation sequentially and in parallel, then time both |
| - [ ] Show that intermediate ops do nothing on their own — add a `peek` and watch when it fires |
