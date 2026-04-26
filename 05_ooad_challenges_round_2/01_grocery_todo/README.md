# Grocery TODO

| Activities |
|------------|
| - [ ] Model the domain: `Item` (name, quantity, unit, category) and `GroceryList` (ordered collection of items) |
| - [ ] Expose the core operations: `add`, `remove`, `markDone`, `listAll`, `listPending` — keep the API small |
| - [ ] Implement undo/redo with the Command pattern: every mutation is a `Command` with `execute` and `undo` |
| - [ ] Keep two stacks (done / undone); a fresh `add` after an undo clears the redo stack |
| - [ ] Make commands granular enough to undo (don't bundle "add five items" into one command unless it's intentional) |
| - [ ] Decide what "done" means and stick to it: stays in the list (struck through) vs moves to a history bucket |
| - [ ] Group items by aisle/category for the print/checkout view (Strategy for the grouping policy) |
| - [ ] Persist the list and the undo/redo history so restarts don't lose it |
| - [ ] Concurrency: two devices editing the same list — pick a conflict policy (last-write-wins, per-item version, CRDT-lite) and write down why |
| - [ ] Tests for: redo after a fresh edit (must be cleared), undo across heterogeneous commands, mark-done on a missing item |
| - [ ] Compare your design with a Memento-based undo and write down which one you'd ship |
