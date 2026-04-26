# Concurrency: Locks and Atomic Variables

| Activities |
|------------|
| - [ ] Protect a shared counter once with `synchronized` and once with `ReentrantLock` — same outcome, different ergonomics |
| - [ ] Try `tryLock`, both with and without a timeout, and decide what to do when you can't grab the lock |
| - [ ] Build a fair `ReentrantLock` and convince yourself it actually changes the order |
| - [ ] Use `lockInterruptibly` and have another thread cancel the wait |
| - [ ] Pick a reader-heavy workload and measure `ReentrantReadWriteLock` against plain synchronization |
| - [ ] Try `StampedLock` with an optimistic read and remember to validate the stamp |
| - [ ] Replace a `wait`/`notify` pair with a `Condition` — bounded buffer is the obvious example |
| - [ ] Reach for `AtomicInteger`, `AtomicLong`, `AtomicBoolean` for simple counters and flags |
| - [ ] Use `AtomicReference` to swap an immutable holder atomically |
| - [ ] Apply CAS by hand: `compareAndSet`, then `updateAndGet` and `accumulateAndGet` |
| - [ ] When contention is high, swap counters for `LongAdder`; build a custom merge with `LongAccumulator` |
| - [ ] Reproduce a race on the unprotected counter so you can see what you fixed |
| - [ ] Reproduce a deadlock with two locks taken in opposite orders |
| - [ ] Fix the deadlock by enforcing a single lock acquisition order |
