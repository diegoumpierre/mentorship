# Guitar Factory

| Activities |
|------------|
| - [ ] Model a `Guitar` with the parts that actually vary: body wood, neck wood, fretboard, pickups, strings, finish, hardware |
| - [ ] Keep a `Model` (LP, Strat, Tele, etc.) as a starting point with sensible defaults — a custom build is a `Model` plus overrides |
| - [ ] Use the Builder pattern for custom configurations and validate at `build()` (incompatible pickups, unavailable wood) |
| - [ ] Price each guitar from its components, not from a flat lookup — a swap of pickup type updates the price automatically |
| - [ ] Track inventory of each part separately; a custom build reserves parts atomically before being accepted |
| - [ ] Reject an order that asks for a part with zero stock instead of silently going negative |
| - [ ] Model the "operating system" / firmware (for guitars with electronics) as another component the build references |
| - [ ] Generate a unique serial number per built guitar and persist the full spec it was built with |
| - [ ] Emit a `GuitarBuilt` event with the spec so downstream systems (warranty, shipping) can react |
| - [ ] Make adding a new model or a new pickup type a one-class change — open/closed for new variations |
| - [ ] Concurrency: two parallel orders must not double-book the last unit of a part — pessimistic or optimistic locking on the part counter |
| - [ ] Tests for: invalid combination, out of stock, full custom build, serial uniqueness |
