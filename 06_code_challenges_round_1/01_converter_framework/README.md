# Converter Framework

| Activities |
|------------|
| - [ ] Define the core abstraction: `Converter<S, T>` with a single `convert(S source)` method — keep it ruthlessly small |
| - [ ] Build a `ConverterRegistry` that resolves a converter by `(sourceType, targetType)` |
| - [ ] Allow registering converters at runtime, programmatically and via a service-loader file |
| - [ ] Walk the type hierarchy on lookup so a converter for `Number → String` also serves `Integer → String` |
| - [ ] Support generic types (e.g., `List<Foo> → List<Bar>`) — resolve element converters at lookup, not per element |
| - [ ] Compose converters automatically: if you can do `A → B` and `B → C`, the registry should hand back an `A → C` |
| - [ ] Detect and reject converter cycles when composing |
| - [ ] Cache resolved converter chains by `(source, target)` so repeated calls don't re-resolve |
| - [ ] Decide what happens for `null` input: pass through, throw, or use a per-converter policy |
| - [ ] Wrap underlying failures in a `ConversionException` with the source type, target type, and offending value |
| - [ ] Handle bean-to-bean conversion: a `BeanConverter` that maps fields by name, with overrides via a small DSL |
| - [ ] Tests for: missing converter, ambiguous (two equally specific candidates), generic element conversion, composed chain, performance of the cached path |
