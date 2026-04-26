# Murder Mystery (clmystery walkthrough)

| Activities |
|------------|
| - [ ] Run through the original `clmystery` end-to-end first (the bash one) and write down the shell commands you actually used — that's your spec |
| - [ ] Recreate the case files on disk: a directory tree of suspects, locations, witnesses, vehicles, alibis |
| - [ ] Re-solve it with one-liners using `grep`, `find`, `awk`, `cut`, `sort`, `uniq`, `xargs` — note where the trick is the pipeline, not the data |
| - [ ] Re-solve it from Java: walk the directory with `Files.walk`, filter with `Files.lines`, narrow suspects step by step |
| - [ ] Build a tiny REPL where each command is a Command (`grep`, `find`, `look`) — Command pattern, easy to extend |
| - [ ] Make the world data-driven: a fresh case is a different folder, no code change |
| - [ ] Add a hint system that nudges without spoiling — track the player's state |
| - [ ] Add a verification step: the player submits a suspect, the game says correct/wrong without revealing the chain |
| - [ ] Generate a fresh randomized case (different murderer, witnesses, alibis) on demand — solver must still solve it |
| - [ ] Keep a session log of the player's commands so the postmortem shows the path they took |
| - [ ] Tests for: case generator always produces a solvable mystery, solver finds the answer for any seed, REPL handles unknown commands gracefully |
| - [ ] Write a short README with the rules and the first hint, the way the original does |
