# Core Bank Ledger

| Activities |
|------------|
| - [ ] Model the domain: `Account`, `Entry` (debit/credit), `Transaction` (a balanced set of entries), `Money` (amount + currency) |
| - [ ] Use double-entry accounting: every transaction's debits must equal its credits — assert this on save, never trust the caller |
| - [ ] Use `BigDecimal` and a documented `RoundingMode` for every monetary value — never `double`, ever |
| - [ ] Make the ledger append-only: an `Entry` is never updated or deleted; corrections are reversing entries |
| - [ ] Compute balances from entries (or maintain a materialized balance per account, refreshed transactionally) |
| - [ ] Enforce currency consistency: an account is single-currency; cross-currency transfers go through an FX leg you can audit |
| - [ ] Idempotency: every transaction takes a client-supplied id; a duplicate replays the previous result, doesn't re-post |
| - [ ] Concurrency: two transfers debiting the same account at the same time — either pessimistic lock the account row or optimistic with `@Version` and retry |
| - [ ] Reject overdrafts based on policy (per account: allowed, soft limit, hard limit) |
| - [ ] Time-travel queries: balance "as of" a past timestamp — falls out of an append-only ledger if you never delete |
| - [ ] Export a statement (CSV/JSON) for an account over a date range |
| - [ ] Tests for: unbalanced transaction (must reject), duplicate idempotency key, concurrent transfers, balance after a chain of postings, reversal correctness |
| - [ ] Document the invariants in one paragraph at the top of the package — future-you will thank you |
