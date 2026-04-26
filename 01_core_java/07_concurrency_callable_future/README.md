# Concurrency: Callable and Future

| Activities |
|------------|
| - [ ] Write a `Callable<V>` that does some work and returns a value — let it throw a checked exception too |
| - [ ] Submit it and grab the result with `Future.get` |
| - [ ] Switch to the timed `get` and trigger a `TimeoutException` on purpose |
| - [ ] Cancel a long-running task with `cancel(true)` and then check `isDone` and `isCancelled` |
| - [ ] Fire off a batch with `invokeAll` and walk through the results |
| - [ ] Use `invokeAny` to take whichever finishes first successfully |
| - [ ] When `get` blows up, unwrap the `ExecutionException.getCause` instead of dumping the wrapper |
| - [ ] Catch `InterruptedException` and remember to restore the interrupt flag — don't swallow it |
| - [ ] Move to `CompletableFuture`: kick things off with `supplyAsync` and `runAsync` |
| - [ ] Chain steps with `thenApply`, `thenAccept`, `thenCompose` |
| - [ ] Merge two independent futures with `thenCombine` |
| - [ ] Wait for a fan-out with `allOf`; race a fan-out with `anyOf` |
| - [ ] Recover from failures with `exceptionally`, inspect both sides with `handle`, attach a side-effect with `whenComplete` |
