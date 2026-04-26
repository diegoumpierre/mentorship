# Ticket System

| Activities |
|------------|
| - [ ] Model the domain: `Show`, `Venue`, `Zone`, `Seat`, `ShowDate`, `Ticket`, `Order` |
| - [ ] A `Show` runs at one or more `ShowDate`s; each date has its own seat map and its own capacity |
| - [ ] Price by `Zone`, not by individual seat — but reserve the specific seat |
| - [ ] Enforce the venue capacity at the show-date level, not just at the venue level |
| - [ ] Sell N tickets in one transaction: either all seats are reserved or none are |
| - [ ] Stop the same seat from being sold twice under concurrent requests — pick a strategy: pessimistic lock on the seat row, optimistic with `@Version`, or unique constraint + retry |
| - [ ] Cover the optimistic path explicitly: handle `OptimisticLockingFailureException` and decide whether to retry or fail the user |
| - [ ] Add a temporary hold (e.g., 5 minutes) from "selected" to "purchased" so the seat doesn't sit forever in a half-baked state |
| - [ ] Expire holds with a scheduled job and free the seats |
| - [ ] Allow cancellation: refund logic + return seats to inventory atomically |
| - [ ] Expose a "seat map" endpoint that shows available / held / sold without lying about the holds about to expire |
| - [ ] Tests: capacity boundary, two concurrent buyers for the same seat, hold expiration, partial-failure rollback |
| - [ ] Reuse the lessons from the existing branch (pessimistic lock, `@Version`, capacity check before selling) and write down which approach you'd ship and why |
