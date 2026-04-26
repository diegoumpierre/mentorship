# Gson

| Activities |
|------------|
| - [ ] Serialize a simple POJO to JSON with `Gson().toJson(obj)` and parse it back with `fromJson` |
| - [ ] Roundtrip a `List<T>` and a `Map<String, T>` using `TypeToken` for the generic case |
| - [ ] Configure a `GsonBuilder` with pretty printing, lenient mode, and `serializeNulls()` |
| - [ ] Pick a naming policy (`FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES`) and confirm field names get rewritten |
| - [ ] Override a single field name with `@SerializedName("foo")` and add aliases with `alternate = {...}` |
| - [ ] Skip fields with `transient` or `@Expose` + `excludeFieldsWithoutExposeAnnotation()` |
| - [ ] Register a custom `TypeAdapter` for a tricky type (say, a domain `Money` or `Instant` in your own format) |
| - [ ] Register a `JsonSerializer` / `JsonDeserializer` pair for a sealed hierarchy (discriminator field on the wire) |
| - [ ] Stream a huge JSON array with `JsonReader` instead of materializing it all in memory |
| - [ ] Write a big payload with `JsonWriter` for the same reason |
| - [ ] Use the `JsonElement` / `JsonObject` tree API for ad-hoc inspection of an unknown payload |
| - [ ] Compare Gson with Jackson on the same model — note where each one is friendlier |
