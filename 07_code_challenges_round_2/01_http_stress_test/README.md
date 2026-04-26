# HTTP Stress Test

| Activities |
|------------|
| - [ ] Define a `Scenario` as code: a sequence of requests, parameters, think-times, assertions |
| - [ ] Pin the load model: closed (N concurrent users) vs open (X requests/sec) — pick the right one for the question you're asking |
| - [ ] Use the JDK's `HttpClient` with `Executors.newVirtualThreadPerTaskExecutor()` (JDK 21+) so you can hold thousands of in-flight calls cheaply |
| - [ ] Drive the load from a coordinator: ramp-up curve, hold, ramp-down — don't slam from zero to peak |
| - [ ] Measure latency with a monotonic clock and an HdrHistogram so percentiles aren't lying to you |
| - [ ] Beware the coordinated-omission trap: schedule by wall clock, not "send next when last finished" |
| - [ ] Capture per-status-code counts, RPS, in-flight requests, error rate, byte throughput |
| - [ ] Aggregate samples in-process and stream a summary to the console every second; persist a final report at the end |
| - [ ] Support data parameterization: pull request payloads from a CSV/JSON feed |
| - [ ] Allow custom assertions per response (status code, JSON path) and count failures separately from errors |
| - [ ] Make the run reproducible: fixed seed, recorded git sha and config, identical runs produce comparable numbers |
| - [ ] Plug in a Prometheus exporter so a real Grafana board shows the run live |
| - [ ] Tests against a deterministic stub server (your own from the previous challenge) so the framework's own correctness isn't network-dependent |
| - [ ] Compare your numbers to `wrk` and `k6` on the same target — calibrate before trusting your own tool |
