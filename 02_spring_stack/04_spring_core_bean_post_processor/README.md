# Spring Core - Bean Post Processor

| Activities |
|------------|
| - [ ] Implement a `BeanPostProcessor` and log every bean as it goes through `postProcessBeforeInitialization` and `postProcessAfterInitialization` |
| - [ ] Use it to inspect a custom annotation on the bean and react (set a field, register a listener, whatever fits) |
| - [ ] Wrap the returned bean in a JDK dynamic proxy from inside the post processor |
| - [ ] Implement a `BeanFactoryPostProcessor` that tweaks a `BeanDefinition` before any instantiation happens |
| - [ ] Show the order of execution: `BeanFactoryPostProcessor` → bean construction → `BeanPostProcessor` callbacks |
| - [ ] Order multiple post processors with `@Order` / `Ordered` and confirm the firing order |
| - [ ] Note which built-in features are themselves post processors (`@Autowired`, `@PostConstruct`, AOP) |
| - [ ] Build a tiny "pseudo-AOP" example that times every method on beans carrying a `@Timed` annotation |
| - [ ] Cover the corner case: post processors are excluded from being processed by other post processors — see what that breaks |
