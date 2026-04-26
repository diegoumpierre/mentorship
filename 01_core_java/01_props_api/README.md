# Properties API

| Activities |
|------------|
| - [ ] Write a `.properties` file by hand, including comments and a couple of escaped characters |
| - [ ] Load it from the classpath with `ClassLoader.getResourceAsStream` and deal with the missing-file case |
| - [ ] Load the same file from disk using `Files.newInputStream` and an explicit UTF-8 reader |
| - [ ] Read keys with `getProperty`, including the version that takes a default |
| - [ ] Convert the raw strings to `int`, `boolean` and `Path` and decide what to do when the value is bogus |
| - [ ] Walk every key/value pair using both `stringPropertyNames` and `entrySet` |
| - [ ] Save the properties back to a file with `store`, with a sensible header line |
| - [ ] Round-trip the same data through `storeToXML` and `loadFromXML` |
| - [ ] Inspect the JVM's own properties via `System.getProperties` (look at `os.name`, `user.home`, `java.version`) |
| - [ ] Set a property at runtime with `System.setProperty`, then drop it with `clearProperty` |
| - [ ] Run the program with `-Dfeature.flag=true` and read it back inside the code |
| - [ ] Throw a clear, typed exception when a required key is missing instead of returning `null` downstream |
