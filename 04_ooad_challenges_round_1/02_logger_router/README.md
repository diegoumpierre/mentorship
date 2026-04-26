# Logger Router

| Activities |
|------------|
| - [ ] Define one stable API: `Logger.log(level, message, context)` — every backend has to fit through this hole |
| - [ ] Model `LogSink` as an interface and write three implementations: filesystem, ELK (HTTP/bulk), and a no-op for tests |
| - [ ] Use a builder so the user wires up multiple sinks fluently: `Logger.builder().toFile(...).toElk(...).build()` |
| - [ ] Make sync vs async a wrapper, not a copy of every sink — a `AsyncSink` that decorates any other sink |
| - [ ] Pick the queue for the async path: bounded vs unbounded, and decide what happens on overflow (drop, block, fall back to sync) |
| - [ ] Drain the async queue cleanly on shutdown — don't lose buffered logs |
| - [ ] Decide on log format separately from the sink: a `LogFormatter` interface (plain, JSON, ECS) |
| - [ ] Add a level filter at the router so each sink can have its own threshold |
| - [ ] Handle backend failures: a sink should not bring the app down — retry, drop, or route to a fallback sink |
| - [ ] Make adding a new sink a one-class change with zero edits to existing code |
| - [ ] Write tests with a fake sink that records what it received |
| - [ ] Compare your design against SLF4J + Logback's appender model and write down what you'd steal and what you'd avoid |
