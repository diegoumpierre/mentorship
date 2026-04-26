# Calendar

| Activities |
|------------|
| - [ ] Model the domain: `User`, `Calendar`, `Meeting` (organizer, participants, range, room), `Availability` |
| - [ ] Use `ZonedDateTime` everywhere — never store local time alone, you'll regret it the first time someone travels |
| - [ ] Book a meeting: validate participant availability and reject overlaps in the same operation |
| - [ ] Cancel a meeting and notify participants (event-driven) |
| - [ ] List meetings for a user inside a date range, sorted, with pagination |
| - [ ] Compute "free time" for a user as the complement of busy intervals — `RangeSet` (Guava) is a clean fit |
| - [ ] Suggest the best time for two people: intersect their free intervals, then rank by heuristic (earliest, longest, fewest interrupted hours) |
| - [ ] Generalize the suggestion to N people (it's the same intersection, but the cost shows up here) |
| - [ ] Respect working hours and time zones per user when computing suggestions |
| - [ ] Handle recurring meetings (RRULE-style) — pick a representation and write down the trade-off (expand vs lazy) |
| - [ ] Concurrency: two organizers booking the same room at the same time — unique constraint on (room, range) and retry on conflict |
| - [ ] Tests for: overlap rejection, suggest with no common slot, suggest across time zones, recurring exception (skip one occurrence) |
