# Observability Framework (Latency)

| Activities |
|------------|
| - [ ] Define the core abstractions: `Timer`, `Sample`, `MeterRegistry`, `Tag` — keep them small and immutable |
| - [ ] Capture a sample with try-with-resources: `try (var s = timer.start()) { ... }` so users can't forget to stop |
| - [ ] Use a monotonic clock (`System.nanoTime`) for measurement and a wall clock only for timestamps |
| - [ ] Pick a histogram representation: bucketed (HdrHistogram) for accuracy, or T-digest for streaming quantiles — own the trade-off |
| - [ ] Compute p50, p95, p99 from the histogram, not from raw samples (don't keep them all) |
| - [ ] Tag metrics by dimensions (endpoint, method, status) and intern tags so the per-sample cost is small |
| - [ ] Make sample recording lock-free on the hot path (per-thread accumulation, periodic merge) |
| - [ ] Add an `AsyncRecorder` that batches writes off the caller's thread for the recording-is-too-slow case |
| - [ ] Build pluggable exporters: a console one for dev, a Prometheus one (`/metrics` endpoint) for prod |
| - [ ] Aggregate over a sliding window (last 1m / 5m / 15m) without keeping every sample |
| - [ ] Instrument something real — an HTTP handler, a JDBC datasource — to prove the API is pleasant |
| - [ ] Tests for: percentile correctness against a known distribution, no allocation on the hot path (use JMH allocation profiler), exporter format snapshot |
| - [ ] Compare your design with Micrometer's `Timer` API and write down which decisions converged and which didn't |
