# Code Generator (YAML-driven)

| Activities |
|------------|
| - [ ] Pin the YAML schema first: types, fields, enums, relations — a schema doc beats endless guessing |
| - [ ] Parse YAML with SnakeYAML into a typed `Model` tree, not raw `Map<String, Object>` |
| - [ ] Validate the model after parsing: unknown types referenced, duplicate names, illegal characters in identifiers |
| - [ ] Resolve references in a separate pass so order in the file doesn't matter |
| - [ ] Pick the output path: hand-rolled string templates vs JavaPoet — JavaPoet for Java output, no contest |
| - [ ] Generate one file per top-level type and write under a configurable target package |
| - [ ] Make the generator deterministic: same input → byte-identical output, so diffs are meaningful |
| - [ ] Add a "dry run" mode that prints what would be written without touching disk |
| - [ ] Don't overwrite hand-edited files: write to a `generated/` source set the user shouldn't edit |
| - [ ] Plug into the build: a Maven mojo or a Gradle task that runs before `compile` |
| - [ ] Generate at least three artifacts from one model: a record/POJO, a builder, a JSON-serializable adapter |
| - [ ] Tests: golden-file tests (input YAML + expected source), validation failure messages, idempotent runs |
| - [ ] Document the extension points so a new artifact type is a one-class addition |
