# Render Template

| Activities |
|------------|
| - [ ] Separate the "what" from the "how": a single `Template` definition (placeholders + structure) and a `Renderer` per output format |
| - [ ] Define the data model the template binds against — a typed `RenderContext` beats a loose `Map<String, Object>` |
| - [ ] Implement `HtmlRenderer`, `PdfRenderer`, `CsvRenderer` behind a common `Renderer` interface |
| - [ ] Use the strategy pattern to pick the renderer at runtime by `MediaType` or extension |
| - [ ] Get escaping right per format: HTML entities, CSV quoting/commas/newlines, PDF special characters |
| - [ ] Decide how to express the template: tokens, a tiny DSL, or lean on an engine (Mustache/Thymeleaf) — and document the trade-off |
| - [ ] Handle missing/extra placeholders explicitly: fail loud, or fill with a default — pick one and stick to it |
| - [ ] Support nested sections and lists in the template, not just flat substitution |
| - [ ] Stream large outputs (especially CSV and PDF) via `OutputStream` — don't materialize everything in memory |
| - [ ] Cache the parsed template; render is the hot path, parsing is not |
| - [ ] Make adding a new format (say, Markdown or XLSX) a one-class change |
| - [ ] Write tests that render the same template into all three formats and check the structure, not the bytes |
