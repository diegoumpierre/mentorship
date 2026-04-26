# Task Framework

| Activities |
|------------|
| - [ ] Define the public surface first: `Task<V>`, `TaskHandle<V>` (cancel, status, result), `TaskExecutor` (submit) |
| - [ ] Build the worker pool yourself — fixed N threads pulling from a `BlockingQueue<Runnable>` is the floor |
| - [ ] Pick the queue deliberately: bounded (backpressure) vs unbounded (memory risk) — bounded by default |
| - [ ] Decide the rejection policy: throw, caller-runs, drop oldest — make it pluggable |
| - [ ] Name your threads with a `ThreadFactory` so dumps are readable |
| - [ ] Support cancellation: a cancel flag the worker checks at safe points; handle the running case differently from the queued case |
| - [ ] Carry exceptions on the `TaskHandle` instead of swallowing them in the worker |
| - [ ] Track per-task state: `QUEUED`, `RUNNING`, `DONE`, `FAILED`, `CANCELLED` — exposed via `TaskHandle.status()` |
| - [ ] Add task priorities with a `PriorityBlockingQueue` and a stable tiebreaker to keep FIFO inside the same priority |
| - [ ] Support task dependencies: a task that starts only after others finish (`whenAllOf`, `whenAnyOf`) |
| - [ ] Shut down cleanly: drain the queue, refuse new submissions, wait with a timeout, then interrupt |
| - [ ] Expose metrics: queue depth, active workers, throughput, task latency histogram |
| - [ ] Tests for: rejection under saturation, cancel of running vs queued task, exception propagation, dependency ordering, clean shutdown without leaks |
| - [ ] Compare your design with `ThreadPoolExecutor` and write down which features you skipped on purpose |
