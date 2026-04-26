# Unused Class Detector

| Activities |
|------------|
| - [ ] Decide the input: a directory of `.class` files, a JAR, or a fat JAR — and write down the assumption |
| - [ ] Read bytecode with ASM (`ClassReader` + a `ClassVisitor`) — don't try to do this with regex on source files |
| - [ ] Build a class graph: every class as a node, every reference as an edge (extends, implements, field type, method signature, opcode operand) |
| - [ ] Visit instructions to catch dynamic references: `INVOKESTATIC`, `INVOKEVIRTUAL`, `LDC` of a class literal, `instanceof`, casts |
| - [ ] Pick the entry points: classes with `main`, JAX-RS / Spring annotations, JNI, things named in `META-INF/services` |
| - [ ] Reachability is a graph traversal from entry points — anything not visited is the candidate set |
| - [ ] Handle reflection deliberately — you can't catch it from bytecode, so allow an explicit allow-list |
| - [ ] Treat `META-INF/services` (`ServiceLoader`) and Spring `@Component` scans as implicit roots |
| - [ ] Don't flag classes referenced only by tests as unused in main — keep main and test classpaths separate |
| - [ ] Output a clean report: list of unused classes grouped by package, with a confidence note ("no reflection seen", "matches a known framework annotation") |
| - [ ] Add a "delete suggestion" mode that prints a `git rm` command per file (don't run it for the user) |
| - [ ] Tests for: a class only referenced by reflection (must be flagged unless allow-listed), a class only referenced by an annotation, a service loaded via SPI, a base class only used by subclasses |
| - [ ] Compare results against IntelliJ's "Unused declaration" inspection on the same project |
