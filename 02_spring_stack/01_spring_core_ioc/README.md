# Spring Core - IoC

| Activities |
|------------|
| - [ ] Stand up an `AnnotationConfigApplicationContext` from a `@Configuration` class and pull a bean out of it |
| - [ ] Do the same with the classic XML `ClassPathXmlApplicationContext` to feel the difference |
| - [ ] Declare a few beans with `@Bean` and a couple with `@Component` + `@ComponentScan` — show that both end up in the container |
| - [ ] Pick a service that depends on two others and let the container wire them up by type |
| - [ ] Resolve an ambiguous dependency with `@Qualifier`, then again with `@Primary`, then with `@Profile` |
| - [ ] Mark a bean as conditional with `@Conditional` (or `@ConditionalOnProperty` if Spring Boot is around) |
| - [ ] Implement an `ApplicationContextAware` bean and grab another bean by name at runtime |
| - [ ] Hook into the lifecycle with `@PostConstruct` / `@PreDestroy` and confirm the order in the logs |
| - [ ] Close the context explicitly and watch destruction callbacks fire |
| - [ ] List every bean name in the context (`getBeanDefinitionNames`) just to see what Spring registered for free |
