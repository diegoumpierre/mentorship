# Teacher's Class Organizer / Optimizer

| Activities |
|------------|
| - [ ] Model the domain: `Teacher`, `Student`, `Subject`, `Room`, `TimeSlot`, `Class` (subject + teacher + room + slot + students) |
| - [ ] Capture the constraints explicitly: a teacher can't be in two rooms at once, a room has a capacity, a student has a fixed weekly load |
| - [ ] Capture preferences separately from constraints: teacher availability, preferred room, no two hard subjects in a row |
| - [ ] Treat scheduling as a constraint-satisfaction problem: hard constraints must hold, soft ones get a cost |
| - [ ] Pick an algorithm and own the trade-off: greedy + repair, simulated annealing, or backtracking — say why |
| - [ ] Detect conflicts as a first-class operation, not a side effect — useful for "validate before save" |
| - [ ] Suggest fixes when a slot is over-constrained (move class X, swap rooms, split the cohort) |
| - [ ] Score a schedule with a weighted cost function — make the weights data, not constants |
| - [ ] Allow manual overrides on top of the optimizer's output — the human is always right |
| - [ ] Persist multiple "candidate" schedules so you can compare and pick |
| - [ ] Re-plan incrementally when one class is moved — don't redo the whole semester |
| - [ ] Tests for: impossible constraints (must report, not crash), single-class change cascades, cost ordering across two schedules |
