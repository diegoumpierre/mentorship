# Ticket System

(7) Build Ticket system, where you should be able

- to sell x number of tickets per different shows
- choose the seat number,
- zone of the venue,
- date and respect
- maximum capacity.

 The Principal problem here is the thing in concurrency

## Activities

| Status | Item | Onde está / o que falta |
|--------|------|-------------------------|
| ☑ | Model the domain: `Show`, `Venue`, `Zone`, `Seat`, `ShowDate`, `Ticket`, `Order` | Todas as entidades modeladas em `model/`; `ShowDate` carrega data e capacidade, `Order` agrupa `Ticket`s com `status`/`total`/`createdAt` |
| ☑ | `Show` roda em uma ou mais `ShowDate`s, cada data com seu próprio seat map e capacidade | `Seat.showDate` referencia o show-date; `ShowDate` tem sua própria `capacity`; `Show` perdeu `date` e `maximumCapacity` |
| ☑ | Preço por `Zone`, mas reserva no assento específico | `Zone.price` (BigDecimal); `ShowServiceImpl#buyTicket` cria `Order`+`Ticket` snapshotando o preço da zona no momento da compra |
| ☑ | Capacidade no nível do show-date, não só no nível do venue | `seatRepository.countByShowDateIdAndSoldTrue` comparado contra `seat.showDate.capacity` dentro de `buyTicket` |
| ☐ | Vender N tickets numa transação (tudo ou nada) | `buyTicket` opera em um único assento |
| ☑ | Não vender o mesmo assento duas vezes sob concorrência | `@Version` em `Seat` + `ConcurrencyBuyTest` valida o cenário |
| ☑ | Tratar `OptimisticLockingFailureException` explicitamente | `buyTicket`/`reserveASeat` capturam `ObjectOptimisticLockingFailureException` e falham o usuário (sem retry) |
| ☑ | Hold temporário (5 min) entre "selecionado" e "comprado" | `RESERVATION_TTL = Duration.ofMinutes(5)` + `reservedBy`/`reservedUntil` em `Seat` |
| ☐ | Job que expira holds e libera os assentos | Hoje só há checagem lazy em `hasActiveReservationBySomeoneElse`; falta um `@Scheduled` |
| ☐ | Cancelamento: refund + devolução do assento ao inventário atomicamente | Não implementado |
| ☐ | Endpoint de seat map (available / held / sold) | Só existem `GET /shows`, `POST /shows/seats/{id}/reserve`, `POST /shows/buy` |
| ~ | Testes: capacity boundary, dois compradores concorrentes, expiração do hold, partial-failure rollback | Capacity boundary, concurrent buyers e hold expiration estão cobertos; `ConcurrencyLoadTest` exercita os três mecanismos com 50 threads; partial-failure rollback depende do "vender N tickets" |
| ~ | Escolher pessimistic vs `@Version` e escrever o porquê | Implementado com `@Version` + capacity check; falta o write-up da decisão |

Legenda: ☑ feito · ~ parcial · ☐ pendente

## Como rodar

```bash
./mvnw spring-boot:run
```

H2 em memória, schema e seed via Liquibase (`src/main/resources/db/changelog/db.changelog-master.sql`). Console em `/h2-console`.

## Endpoints

- `GET /shows` — lista shows do seed
- `POST /shows/seats/{seatId}/reserve?userId={id}` — segura o assento por 5 minutos
- `POST /shows/buy?userId={id}` body `{"seat":{"id":N}}` — compra o assento

## Concorrência — abordagem atual

`@Version` no `Seat` (optimistic lock). Em `buyTicket`/`reserveASeat`, se `save` lança `ObjectOptimisticLockingFailureException` o usuário recebe falha (sem retry). Capacity check (`countByShowDateIdAndSoldTrue >= showDate.capacity`) acontece dentro da mesma transação antes do `save`. Cobertura em `ConcurrencyBuyTest` garante que só uma das duas threads consegue comprar o mesmo assento. `ConcurrencyLoadTest` roda 50 threads em três cenários (mesmo assento → 1 vencedor pelo `@Version`; assento já reservado → 0 compras pelo TTL; vários assentos do mesmo show-date → vendas limitadas pela capacidade).

## TODO interno (já feito)

- [x] persistência — H2
- [x] `buyTicket` em `ShowServiceImpl`
- [x] `listAllShow` retornando dados reais
- [x] `reserveASeat` com TTL
- [x] endpoints do controller (list, reserve, buy)
- [x] concorrência com `@Version`
- [x] validação da capacidade do show antes de vender
- [x] testes unitários e de integração para os caminhos acima
