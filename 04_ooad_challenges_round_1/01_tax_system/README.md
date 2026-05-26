# Tax System

| Activities |
|------------|
| - [x] Map out the domain on paper before writing code: `Product`, `State`, `TaxRate`, `EffectivePeriod`, `Invoice` |
| - [x] Decide what makes a tax rate unique — (product category × state × year) is the obvious key, but check whether city or product-id can override it |
| - [x] Pick a strategy for "rate as of a date": effective-from / effective-to ranges, with a lookup that picks the right one |
| - [x] Sketch the public API first: `taxFor(product, state, when)` and `totalFor(invoice)` — design the call sites before the internals |
| - [x] Avoid the giant `if/else` over states: use a `TaxRule` interface and let each state plug its own rule |
| - [x] Make adding a new state or year a no-touch change for existing code (open/closed) |
| - [x] Cache resolved rates by key, but invalidate when a new effective period is loaded |
| - [x] Handle compound taxes (federal + state + city) by composing rules, not by hardcoding them |
| - [x] Round money explicitly with `BigDecimal` and a documented `RoundingMode` — no `double` |
| - [x] Load rates from an external source (JSON/CSV/DB) so the engine isn't tied to the data |
| - [x] Write tests for: same product different states, same state different years, missing rate, overlapping periods |
| - [x] Document the assumption you made when a date falls in a gap between two periods |

## Notes on effective periods

Tax rates are scoped by `EffectivePeriod`, modeled as a half-open interval `[from, to)`:
- `from` is inclusive — a rate becomes valid on that date
- `to` is exclusive — the day equal to `to` already falls under the next period
- `to = null` means open-ended: the rate stays valid until a new period replaces it

Two situations the engine treats as data problems and fails fast on:
- **Gap** between two periods (e.g. `[2024-01-01, 2024-07-01)` then `[2025-01-01, null)`): a lookup on `2024-10-01` finds zero applicable rates. The engine raises `IllegalStateException("Sem tax rate vigente...")`. The assumption is that a gap is never a "rate = 0%" — it's missing data and should be treated as such.
- **Overlap** between two periods on the same `(product, state)`: the engine raises `IllegalStateException("Mais de uma tax rate vigente...")`. The DB also enforces uniqueness of `(product_id, state_code, effective_from)`, so the only way to land in this state is by inserting two rows with different `effective_from` whose ranges intersect.

Compound layers (federal, state-via-DB, JSON) are summed independently. Each layer must return 0 or 1 applicable rule for a given `(product, state, when)`; if at least one layer contributes, the engine returns the sum of percents. If no layer contributes, the same "no rate" error is raised.
