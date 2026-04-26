# Quartz

| Activities |
|------------|
| - [ ] Set up a `Scheduler` from a `SchedulerFactory` and start it cleanly |
| - [ ] Write a `Job` and pass parameters through the `JobDataMap` |
| - [ ] Schedule it with a `SimpleTrigger` (run every X seconds, N times) |
| - [ ] Schedule it again with a `CronTrigger` (`0 */15 * * * ?`) and double-check the cron field meaning |
| - [ ] Pause, resume and unschedule a job at runtime |
| - [ ] Mark the job `@DisallowConcurrentExecution` and prove two firings can't overlap |
| - [ ] Mark it `@PersistJobDataAfterExecution` so updated data carries to the next run |
| - [ ] Set a misfire policy and provoke a misfire (downtime, blocked thread pool) to see it kick in |
| - [ ] Plug in a `JDBCJobStore` so jobs survive restarts (use Postgres or H2) |
| - [ ] Run two scheduler instances against the same store and watch clustering distribute the jobs |
| - [ ] Register a `JobListener` and a `TriggerListener` to log lifecycle events |
| - [ ] Wire Quartz into Spring Boot using the starter and use `@Bean JobDetail` / `Trigger` definitions |
| - [ ] Compare Quartz with `@Scheduled` and write down when each one earns its keep |
