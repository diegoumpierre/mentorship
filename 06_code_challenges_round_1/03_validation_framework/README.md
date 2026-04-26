# Validation Framework

| Activities |
|------------|
| - [ ] Define the core annotations: `@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Size`, `@Pattern`, `@Email` |
| - [ ] One annotation, one `Validator<A, T>` — wire them with a meta-annotation (`@Constraint(by = NotNullValidator.class)`) |
| - [ ] Build a `Validator.validate(Object target)` that returns a `Set<Violation>` (path, message, invalid value) — never throw on failure |
| - [ ] Walk the object graph: validate fields, then nested objects (mark them with `@Valid`), then collections element-by-element |
| - [ ] Cache the resolved constraints per class — reflection at startup, not per call |
| - [ ] Resolve messages from a `MessageSource` with placeholders (`"size must be between {min} and {max}"`) and locale support |
| - [ ] Support validation groups (`Default.class`, `Create.class`, `Update.class`) so the same model has context-specific rules |
| - [ ] Allow composed annotations: `@Username` = `@NotBlank` + `@Size(3, 20)` + `@Pattern(...)` |
| - [ ] Add cross-field validation at the class level (`@Constraint` on the type) for things like "endDate after startDate" |
| - [ ] Make adding a custom annotation a one-class change (annotation + validator), no edits to the engine |
| - [ ] Tests for: nested objects, list of objects, message interpolation, group switching, cross-field rule |
| - [ ] Compare your design with Jakarta Validation (Hibernate Validator) and write down what you'd change with hindsight |
