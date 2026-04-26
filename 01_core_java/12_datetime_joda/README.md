# Date Time API + Joda Time

| Activities |
|------------|
| - [ ] Get comfortable with `LocalDate`, `LocalTime`, `LocalDateTime` |
| - [ ] Add a time zone with `ZonedDateTime` and an offset with `OffsetDateTime` |
| - [ ] Use `Instant` for "machine time" and convert to and from epoch seconds/millis |
| - [ ] Pick the right amount type: `Duration` for hours/minutes, `Period` for years/months/days |
| - [ ] Format and parse with the predefined `DateTimeFormatter` constants |
| - [ ] Build a custom pattern with `ofPattern` and try it against a couple of locales |
| - [ ] Convert a `ZonedDateTime` between zones with `withZoneSameInstant` (try `America/Sao_Paulo` ↔ `UTC`) |
| - [ ] Use `TemporalAdjusters.firstDayOfMonth` and `next(DayOfWeek.MONDAY)` |
| - [ ] Write your own `TemporalAdjuster` for something domain-specific (next business day, say) |
| - [ ] Do arithmetic with `plus`, `minus`, `with`, mixed with `ChronoUnit` |
| - [ ] Compare with `isBefore`, `isAfter`, `equals` — and watch out for the offset / zone gotchas |
| - [ ] Pull in Joda Time and try `DateTime`, `LocalDate`, `Interval`, plus formatting via `DateTimeFormat` |
| - [ ] Write a small "before / after" of the same logic in Joda and `java.time` |
| - [ ] Convert a legacy `java.util.Date` to `Instant`, and a `Calendar` to `ZonedDateTime`, when you can't avoid the old API |
