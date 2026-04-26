# Configure Transaction management with JPA

| Activities |
|------------|
| - [ ] Wire up `JpaTransactionManager` (Spring Boot does it for you, but go look at the auto-config to see what's actually registered) |
| - [ ] Mark a service method with `@Transactional` and prove a thrown `RuntimeException` rolls everything back |
| - [ ] Show that a checked exception does NOT roll back by default — and fix it with `rollbackFor` |
| - [ ] Walk through every propagation level: `REQUIRED`, `REQUIRES_NEW`, `NESTED`, `SUPPORTS`, `MANDATORY`, `NEVER` (one example each) |
| - [ ] Walk through isolation levels (`READ_COMMITTED`, `REPEATABLE_READ`, `SERIALIZABLE`) and reproduce a phenomenon that each prevents |
| - [ ] Reproduce the classic self-invocation gotcha (one `@Transactional` method calling another inside the same bean) and fix it |
| - [ ] Use `readOnly = true` for read paths and check the JDBC connection metadata to confirm |
| - [ ] Set a `timeout` and provoke a `TransactionTimedOutException` |
| - [ ] Hook into commit/rollback with `TransactionSynchronization.afterCommit` (e.g., publish a domain event only after the row is durable) |
| - [ ] Trigger an `OptimisticLockingFailureException` with `@Version` and decide on a retry strategy |
| - [ ] Acquire a pessimistic lock with `@Lock(LockModeType.PESSIMISTIC_WRITE)` and force two transactions to contend |
| - [ ] Watch SQL with `spring.jpa.show-sql` (or a `datasource-proxy`) and confirm what's actually being flushed and when |
