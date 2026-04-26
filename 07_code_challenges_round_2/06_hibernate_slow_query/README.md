# Hibernate Slow Query Detector

| Activities |
|------------|
| - [ ] Pick the integration point: a JDBC `DataSource` proxy (`datasource-proxy` / `p6spy`) or a Hibernate `StatementInspector` — try both and write down the trade-off |
| - [ ] Time every executed statement on a monotonic clock; record SQL, parameters, duration, caller stack |
| - [ ] Flag anything past a configurable threshold (e.g., `slow.query.threshold=200ms`) and log it with a stable marker |
| - [ ] Strip / hash bound parameters in logs so PII doesn't leak |
| - [ ] Detect the N+1 pattern: same SQL template fired N times within one transaction → emit one warning per pattern, not N |
| - [ ] Detect missing `fetch` joins on a relation that was lazily resolved more than X times in one transaction |
| - [ ] Hook into Hibernate's `SessionFactory` lifecycle to attach the inspector cleanly via auto-config |
| - [ ] Cap the in-memory ring buffer of recent slow queries — bounded, never unbounded |
| - [ ] Expose the buffer through a tiny endpoint (`/diagnostics/slow-queries`) for ad-hoc inspection |
| - [ ] Export per-query histograms (count, p95) through Micrometer so a dashboard can plot trends |
| - [ ] Make sampling configurable (1.0 in dev, 0.01 in prod) so the cost is bounded under load |
| - [ ] Tests with H2 / Postgres + Testcontainers: trigger an N+1 with a JPA mapping and assert the detector flags it; trigger a slow query with `pg_sleep` and assert the threshold logic |
| - [ ] Compare your output with what Hibernate's `org.hibernate.SQL_SLOW` already provides — write down what you added on top |
