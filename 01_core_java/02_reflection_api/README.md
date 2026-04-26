# Reflection API

| Activities |
|------------|
| - [ ] Get a `Class<?>` three different ways: literal, `getClass()` and `Class.forName` |
| - [ ] Compare `getDeclaredFields` with `getFields` on a class that has a parent — see what each one returns |
| - [ ] Do the same comparison for methods and constructors |
| - [ ] Read a private field, then change it, then change a `static` one |
| - [ ] Look up a method by name and parameter types and call it on an instance |
| - [ ] Call a static method the same way |
| - [ ] When the invoked method blows up, unwrap `InvocationTargetException` so the real cause shows up |
| - [ ] Build an object through `Constructor.newInstance`, including a private constructor |
| - [ ] Read modifiers with `Modifier.isPublic`, `isStatic`, `isFinal` |
| - [ ] Pull generic type info from a field — resolve the `ParameterizedType` arguments |
| - [ ] Read an annotation off a class, a method and a field at runtime |
| - [ ] Time a reflective call against a direct one and write down the gap |
| - [ ] Hit a JPMS module boundary on purpose and see what the access error looks like |
