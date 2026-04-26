# Tax System

| Activities |
|------------|
| - [ ] Map out the domain on paper before writing code: `Product`, `State`, `TaxRate`, `EffectivePeriod`, `Invoice` |
| - [ ] Decide what makes a tax rate unique — (product category × state × year) is the obvious key, but check whether city or product-id can override it |
| - [ ] Pick a strategy for "rate as of a date": effective-from / effective-to ranges, with a lookup that picks the right one |
| - [ ] Sketch the public API first: `taxFor(product, state, when)` and `totalFor(invoice)` — design the call sites before the internals |
| - [ ] Avoid the giant `if/else` over states: use a `TaxRule` interface and let each state plug its own rule |
| - [ ] Make adding a new state or year a no-touch change for existing code (open/closed) |
| - [ ] Cache resolved rates by key, but invalidate when a new effective period is loaded |
| - [ ] Handle compound taxes (federal + state + city) by composing rules, not by hardcoding them |
| - [ ] Round money explicitly with `BigDecimal` and a documented `RoundingMode` — no `double` |
| - [ ] Load rates from an external source (JSON/CSV/DB) so the engine isn't tied to the data |
| - [ ] Write tests for: same product different states, same state different years, missing rate, overlapping periods |
| - [ ] Document the assumption you made when a date falls in a gap between two periods |
