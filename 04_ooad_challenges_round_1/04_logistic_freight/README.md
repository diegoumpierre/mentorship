# Logistic Freight

Sistema de frete com preço dinâmico por volume, peso, distância e tipo de transporte.
A cotação roda contra todos os modos elegíveis e devolve `(modo, preço, ETA)` pro chamador escolher.

## Desenho

- **Domínio tipado** (`model`): `Weight`, `Volume`, `Dimensions` e `Location` são value objects.
  Dinheiro é `BigDecimal` ponta a ponta, peso/volume nunca andam como `double` cru.
- **Modos selados** (`mode`): `TransportMode` é uma `sealed interface` com `Boat`, `Truck`, `Rail`
  (e `Airplane`, adicionado depois pra provar o open/closed). Cada modo carrega sua própria
  constraint de peso e de rota e devolve os motivos de rejeição — nada de `switch`.
- **Preço como cadeia** (`pricing`): `PricingStrategy` por modo, montada como lista de
  `PriceComponent`: base + volume + peso + distância + combustível + perigoso. Combustível é
  percentual sobre o subtotal corrente; perigoso entra como sobretaxa no fim.
- **Tarifas externas** (`rates`): vêm de `rates.json` e podem mudar diariamente sem redeploy.
  Cada `RateSnapshot` carrega o carimbo `as-of`. O `CachingRateProvider` cacheia o snapshot e
  **recusa** cotar com dado vencido (`StaleRatesException`) em vez de usar stale silenciosamente.
- **Cotação** (`quote`): `QuoteService` filtra os modos inelegíveis cedo (oversize, sem porto,
  sem ferrovia no destino) antes de qualquer cálculo de preço, e ordena o resultado por preço.

## Reprodutibilidade

Dada a mesma carga e o mesmo snapshot de tarifa, a cotação devolve sempre o mesmo número
(`quoteAll(shipment, snapshot)`). Sem snapshot, usa o `RateProvider` corrente.

## Atividades

| Activities |
|------------|
| - [x] Model the shipment: dimensions, weight, declared value, origin, destination, hazardous flag |
| - [x] Model the transport modes as a sealed hierarchy: `Boat`, `Truck`, `Rail` — each with its own constraints (max weight, route compatibility) |
| - [x] Treat pricing as a pluggable `PricingStrategy` per mode, not a switch statement |
| - [x] Build the price as a chain of components: base + per-volume + per-weight + surcharges (fuel, distance, hazard) |
| - [x] Externalize rates so they can change daily without a redeploy (config file, DB, or remote service) |
| - [x] Cache rate snapshots and mark them with an "as-of" timestamp; never silently use stale data for a quote |
| - [x] Quote across all eligible modes for a shipment and let the caller pick — return a list of `(mode, price, ETA)` |
| - [x] Reject ineligible combinations early (oversize for truck, no rail at the destination) instead of failing inside the price calc |
| - [x] Make a quote idempotent and reproducible: given a shipment and a rate snapshot, always the same number |
| - [x] Use `BigDecimal` for money throughout, and keep weights/volumes in a typed value object — no raw `double` |
| - [x] Add a new transport mode without touching existing code (open/closed) |
| - [x] Tests for: edge dimensions, ineligible mode, all modes priced, rate change between two consecutive quotes |
