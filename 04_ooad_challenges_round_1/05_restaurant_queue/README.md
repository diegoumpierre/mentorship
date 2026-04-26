# Restaurant Queue

| Activities |
|------------|
| - [ ] Model the domain: `Dish` (with prep time and station), `Order` (a list of dishes), `Station` (grill, fry, cold), `Kitchen` (set of stations) |
| - [ ] Treat each station as having a fixed parallelism (e.g., two grill spots) — total throughput is the bottleneck of the slowest station |
| - [ ] When a new order arrives, estimate completion by simulating placement on the current schedule, not by summing dish times |
| - [ ] Expose `eta(orderId)` and `eta(dish, now)` — both should be cheap to call, so keep an incremental projection |
| - [ ] Recompute ETAs when an order is added, finished, or cancelled — and only for the affected stations |
| - [ ] Handle priorities (VIP, dine-in vs takeout) without breaking FIFO inside the same priority class |
| - [ ] Account for prep dependencies inside an order: don't serve the steak before the salad is ready |
| - [ ] Move the clock with an injected `Clock` so the whole thing is testable without `Thread.sleep` |
| - [ ] Persist the queue state so a restart doesn't lose in-flight orders |
| - [ ] Emit events on each transition (queued, started, ready, served) for the UI/printer |
| - [ ] Tests for: empty kitchen ETA, station saturation, parallel dishes in one order, late cancellation |
| - [ ] Compare two queueing policies (FIFO vs shortest-job-first) and write down which one trades fairness for throughput |
