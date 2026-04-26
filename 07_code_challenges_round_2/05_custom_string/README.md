# Custom String

| Activities |
|------------|
| - [ ] Decide the internal representation: `char[]`, `byte[]` UTF-8, or `byte[]` Latin-1+UTF-16 (compact strings, JDK 9+ style) — own the trade-off |
| - [ ] Make the type immutable: final fields, defensive copy on construction, never expose the backing array |
| - [ ] Cache `length` and `hashCode` (lazily) — both are called all over the place |
| - [ ] Implement `length()`, `charAt(int)`, `isEmpty()` |
| - [ ] Implement `toCharArray()` returning a defensive copy |
| - [ ] Implement `iterator()` (or make the type `Iterable<Character>`) |
| - [ ] Implement `forEach(Consumer<Character>)` on top of the iterator |
| - [ ] Implement `reverse()` returning a new instance, not mutating |
| - [ ] Implement `equals(Object)` and `hashCode()` consistent with each other (same algorithm `String` uses) |
| - [ ] Implement `indexOf(int ch)` and `indexOf(MyString s)` — naive search first, then look at what `String` actually does |
| - [ ] Implement `substring(int, int)` with bounds checks and a clear `StringIndexOutOfBoundsException` |
| - [ ] Implement `replace(char, char)` and `replace(MyString, MyString)` |
| - [ ] Implement `trim()` against ASCII whitespace; mention `strip()` and Unicode whitespace as the more correct version |
| - [ ] Implement `toJson()` — escape `"`, `\`, `\n`, `\t`, control chars, surrogate pairs |
| - [ ] Tests against `java.lang.String` as the oracle: random inputs, both produce the same answer |
| - [ ] Skim the JDK source for `java.lang.String` after you're done and note three things you'd change in your version |
