# Logistic Freight

| Activities |
|------------|
| - [ ] Model the shipment: dimensions, weight, declared value, origin, destination, hazardous flag |
| - [ ] Model the transport modes as a sealed hierarchy: `Boat`, `Truck`, `Rail` — each with its own constraints (max weight, route compatibility) |
| - [ ] Treat pricing as a pluggable `PricingStrategy` per mode, not a switch statement |
| - [ ] Build the price as a chain of components: base + per-volume + per-weight + surcharges (fuel, distance, hazard) |
| - [ ] Externalize rates so they can change daily without a redeploy (config file, DB, or remote service) |
| - [ ] Cache rate snapshots and mark them with an "as-of" timestamp; never silently use stale data for a quote |
| - [ ] Quote across all eligible modes for a shipment and let the caller pick — return a list of `(mode, price, ETA)` |
| - [ ] Reject ineligible combinations early (oversize for truck, no rail at the destination) instead of failing inside the price calc |
| - [ ] Make a quote idempotent and reproducible: given a shipment and a rate snapshot, always the same number |
| - [ ] Use `BigDecimal` for money throughout, and keep weights/volumes in a typed value object — no raw `double` |
| - [ ] Add a new transport mode without touching existing code (open/closed) |
| - [ ] Tests for: edge dimensions, ineligible mode, all modes priced, rate change between two consecutive quotes |
