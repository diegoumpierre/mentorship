# Spring Core - @Value

| Activities |
|------------|
| - [ ] Inject a literal string with `@Value("hello")` just to see the simplest form |
| - [ ] Inject a property with `@Value("${app.name}")` from `application.properties` |
| - [ ] Provide a default with `@Value("${app.timeout:30}")` and prove it kicks in when the key is missing |
| - [ ] Inject typed values (`int`, `boolean`, `Duration`, `Path`) and let Spring do the conversion |
| - [ ] Inject a comma-separated list straight into a `List<String>` |
| - [ ] Use SpEL: `@Value("#{systemProperties['user.home']}")`, then a small expression with operators |
| - [ ] Reference another bean from SpEL: `@Value("#{otherBean.someProperty}")` |
| - [ ] Compare `@Value` with `@ConfigurationProperties` for grouped settings — and pick the right tool |
| - [ ] Show that `@Value` can't read a property loaded after the bean was created (good place to mention `@RefreshScope`) |
| - [ ] Resolve a property from a non-default source by registering a custom `PropertySource` |
| - [ ] Override a property at runtime with `-Dapp.name=...` and confirm it wins |
