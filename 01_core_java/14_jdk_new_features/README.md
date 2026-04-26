# New JDK Features

| Activities |
|------------|
| - [ ] Records (JDK 16): declare one, add a compact constructor that validates, then add a static factory |
| - [ ] Sealed classes and interfaces (JDK 17): build a hierarchy with `permits` and tag the children as `final`, `sealed` or `non-sealed` |
| - [ ] Pattern matching for `instanceof` (JDK 16): bind the variable in a single line |
| - [ ] Pattern matching for `switch` (JDK 21): match by type, add a `when` guard, handle `null` |
| - [ ] Record patterns (JDK 21): deconstruct a record inside `switch`, then nest one inside another |
| - [ ] Text blocks (JDK 15): write a multi-line JSON/SQL snippet and watch how the indentation gets stripped |
| - [ ] Local-variable type inference (JDK 10): use `var` where it helps, skip it where it hurts readability |
| - [ ] Switch expressions (JDK 14): the arrow form, plus `yield` for the multi-line case |
| - [ ] Virtual threads (JDK 21): start one with `Thread.ofVirtual` and run a workload on `Executors.newVirtualThreadPerTaskExecutor` |
| - [ ] Structured concurrency (JDK 21+, preview): try `StructuredTaskScope.ShutdownOnFailure` |
| - [ ] Scoped values (JDK 21+, preview): replace a `ThreadLocal` with `ScopedValue` and notice the read-only contract |
| - [ ] Sequenced collections (JDK 21): use `getFirst`, `getLast`, `reversed` on `List` and `LinkedHashSet` |
| - [ ] Unnamed variables and patterns (JDK 21+): drop in `_` for the bits you don't care about |
| - [ ] Stream gatherers (JDK 22+): try the built-ins (`windowFixed`, `windowSliding`, `fold`) and write one of your own |
| - [ ] Foreign Function & Memory API (JDK 22): allocate off-heap with `Arena`, then call into a native function via `Linker` |
| - [ ] Java 25 — compact source files and instance `main` methods: write a one-file program with no class declaration |
| - [ ] Java 25 — module imports: pull a whole module in with `import module ...` and skip the per-package noise |
| - [ ] Java 25 — primitive types in patterns, `instanceof` and `switch`: pattern-match directly against `int`, `long`, etc. |
| - [ ] Java 25 — flexible constructor bodies: run statements before `super()` / `this()` and validate arguments early |
| - [ ] Java 25 — stable values: replace a `volatile`-guarded lazy field with `StableValue` |
| - [ ] Java 25 — scoped values finalized: drop the preview flags and migrate the JDK 21 example |
| - [ ] Java 25 — structured concurrency updates: re-do the JDK 21 example against the newer API surface |
| - [ ] Java 25 — generational GC improvements: run a workload under generational Shenandoah / ZGC and skim the GC logs |
