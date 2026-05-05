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
| ☑ | Vender N tickets numa transação (tudo ou nada) | `buyTickets(user, seatIds)` carrega+valida todos antes de salvar; qualquer falha (assento sumiu, vendido, capacidade estourada, optimistic lock) marca a tx como rollback-only e nenhuma venda persiste |
| ☑ | Não vender o mesmo assento duas vezes sob concorrência | `@Version` em `Seat` + `ConcurrencyBuyTest` valida o cenário |
| ☑ | Tratar `OptimisticLockingFailureException` explicitamente | `buyTicket`/`reserveASeat` capturam `ObjectOptimisticLockingFailureException` e falham o usuário (sem retry) |
| ☑ | Hold temporário (5 min) entre "selecionado" e "comprado" | `RESERVATION_TTL = Duration.ofMinutes(5)` + `reservedBy`/`reservedUntil` em `Seat` |
| ☑ | Job que expira holds e libera os assentos | `ReservationExpirationJob` com `@Scheduled` (1 em 1 min) limpa `reservedBy`/`reservedUntil` de seats não vendidos com `reservedUntil < now` |
| ☑ | Cancelamento: refund + devolução do assento ao inventário atomicamente | `POST /orders/{id}/cancel` numa transação só: marca `Order.status=CANCELLED` e libera os assentos vinculados |
| ☑ | Endpoint de seat map (available / held / sold) | `GET /shows/dates/{showDateId}/seats` retorna cada assento com `status` (AVAILABLE/HELD/SOLD) e `zoneName` |
| ☑ | Testes: capacity boundary, dois compradores concorrentes, expiração do hold, partial-failure rollback | `ConcurrencyLoadTest` exercita os três mecanismos com 50 threads; `MultiPurchaseIntegrationTest` cobre o partial-failure rollback (assento já vendido no meio, assento inexistente) |
| ☑ | Escolher pessimistic vs `@Version` e escrever o porquê | Optimistic com `@Version`; raciocínio na seção "Por que optimistic e não pessimistic" abaixo |

Legenda: ☑ feito · ~ parcial · ☐ pendente

## Como rodar

```bash
./mvnw spring-boot:run
```

H2 em memória, schema e seed via Liquibase (`src/main/resources/db/changelog/db.changelog-master.sql`). Console em `/h2-console`.

## Endpoints

- `GET /shows` — lista shows do seed
- `GET /shows/dates/{showDateId}/seats` — seat map com status por assento
- `POST /shows/seats/{seatId}/reserve?userId={id}` — segura o assento por 5 minutos
- `POST /shows/buy?userId={id}` body `{"seat":{"id":N}}` — compra o assento
- `POST /shows/buy-many?userId={id}` body `{"seatIds":[1,2]}` — compra vários numa transação (tudo ou nada)
- `POST /orders/{orderId}/cancel` — cancela a order e libera os assentos

## Concorrência — abordagem atual

`@Version` no `Seat` (optimistic lock). Em `buyTicket`/`reserveASeat`, se `save` lança `ObjectOptimisticLockingFailureException` o usuário recebe falha (sem retry). Capacity check (`countByShowDateIdAndSoldTrue >= showDate.capacity`) acontece dentro da mesma transação antes do `save`. Cobertura em `ConcurrencyBuyTest` garante que só uma das duas threads consegue comprar o mesmo assento. `ConcurrencyLoadTest` roda 50 threads em três cenários (mesmo assento → 1 vencedor pelo `@Version`; assento já reservado → 0 compras pelo TTL; vários assentos do mesmo show-date → vendas limitadas pela capacidade).

## Por que optimistic e não pessimistic

A escolha foi `@Version` (optimistic) em vez de `SELECT ... FOR UPDATE` (pessimistic) por três motivos práticos para esse domínio:

- **Conflito real é raro.** A esmagadora maioria das compras é em assentos diferentes; só dá conflito quando dois usuários miram o mesmo assento ao mesmo tempo. Tomar lock em todo seat só pra blindar o caso raro paga um preço caro no caso comum (espera, contention, deadlock potencial entre transações que tocam vários seats em ordens diferentes).
- **Conexão segurada por 5 minutos não é uma opção.** O fluxo do produto tem hold de 5 min entre "selecionar" e "comprar". Se a reserva fosse um lock pessimista de DB, a transação teria que ficar aberta esse tempo todo segurando uma conexão do pool — inviável. Por isso o hold é modelado como dado (`reservedBy`/`reservedUntil`) e não como lock; o `@Version` cobre só a janela curta da escrita final.
- **Falha tem ação clara.** Quando o `@Version` falha, o usuário simplesmente vê "assento indisponível" e escolhe outro. Não precisa de retry automático: ou o assento sumiu mesmo (perdeu pra outro comprador) e o retry continuaria falhando, ou tem outros assentos disponíveis e o próprio usuário decide.

Limitação conhecida: o capacity check (`countByShowDateIdAndSoldTrue`) tem race window — duas threads podem ler `count = N-1`, ambas passar no `< capacity`, e ambas commitarem. Hoje isso só não estoura porque o número de assentos físicos do show-date já casa com a capacidade configurada (na prática quem segura é o `@Version` por seat). Se um dia a capacidade for menor que o total de seats, vai precisar virar um lock no `ShowDate` ou `SELECT ... FOR UPDATE` na contagem.

## TODO interno (já feito)

- [x] persistência — H2
- [x] `buyTicket` em `ShowServiceImpl`
- [x] `listAllShow` retornando dados reais
- [x] `reserveASeat` com TTL
- [x] endpoints do controller (list, reserve, buy)
- [x] concorrência com `@Version`
- [x] validação da capacidade do show antes de vender
- [x] testes unitários e de integração para os caminhos acima
