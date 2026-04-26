# Concurrency: ExecutorService

| Activities |
|------------|
| - [ ] Spin up a fixed pool, a cached pool and a single-thread executor through the `Executors` factories |
| - [ ] Schedule something one-off with `schedule`, then a recurring job with `scheduleAtFixedRate` |
| - [ ] Compare `scheduleAtFixedRate` against `scheduleWithFixedDelay` — pick the wrong one on purpose to see the difference |
| - [ ] Submit a plain `Runnable`, then submit a `Runnable` with a result tacked on |
| - [ ] Shut down cleanly: call `shutdown`, then `awaitTermination` with a timeout, then fall back to `shutdownNow` |
| - [ ] Plug in a custom `ThreadFactory` so threads have meaningful names in the dump |
| - [ ] Set thread priority and the daemon flag through that factory |
| - [ ] Build a `ThreadPoolExecutor` from scratch and pick a queue: `ArrayBlockingQueue` vs `LinkedBlockingQueue` |
| - [ ] Try every rejection policy in turn: `AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`, `DiscardOldestPolicy` |
| - [ ] Switch to `Executors.newVirtualThreadPerTaskExecutor` (JDK 21+) and run the same workload again to see the throughput difference |
