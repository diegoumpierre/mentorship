# Spring Core - Bean Scopes

| Activities |
|------------|
| - [ ] Declare a default singleton bean and prove there's only one instance by injecting it twice |
| - [ ] Switch the same bean to `@Scope("prototype")` and watch a fresh instance show up on every lookup |
| - [ ] Inject a prototype into a singleton — see it stay frozen on the first instance |
| - [ ] Fix the singleton-to-prototype problem with `ObjectProvider` (the modern way) |
| - [ ] Fix it again with method injection / `@Lookup` |
| - [ ] Fix it once more with `proxyMode = TARGET_CLASS` and explain when each option fits |
| - [ ] Try the web scopes too: `request`, `session`, `application` |
| - [ ] Define a custom scope (a thread-bound one is the textbook example) and register it via `CustomScopeConfigurer` |
| - [ ] Confirm singleton beans are eager by default and prototype beans are lazy |
| - [ ] Toggle `@Lazy` on a singleton and verify it isn't created until first use |
