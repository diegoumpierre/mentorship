# Spring Security: Authorize HTTP Requests

| Activities |
|------------|
| - [ ] Define a `SecurityFilterChain` bean using `HttpSecurity` (the lambda DSL is the current style) |
| - [ ] Open a few public endpoints with `requestMatchers("/public/**").permitAll()` |
| - [ ] Lock down everything else with `anyRequest().authenticated()` |
| - [ ] Restrict an endpoint by role with `hasRole("ADMIN")` and another by authority with `hasAuthority("SCOPE_read")` |
| - [ ] Match by HTTP method too: `requestMatchers(HttpMethod.POST, "/api/**").hasRole("WRITER")` |
| - [ ] Combine rules with `access(...)` and a `AuthorizationManager` for the cases the DSL doesn't cover |
| - [ ] Set up two filter chains in the same app (`@Order(1)` for `/api/**`, `@Order(2)` for the rest) and confirm each chain handles its own paths |
| - [ ] Wire form login and HTTP basic on the right chain |
| - [ ] Wire JWT/OAuth2 resource-server on the API chain (`oauth2ResourceServer(jwt -> ...)`) |
| - [ ] Customize the access-denied response (custom `AccessDeniedHandler`) and the auth-entry-point for 401s |
| - [ ] Add method security with `@EnableMethodSecurity` and use `@PreAuthorize` / `@PostAuthorize` on a service |
| - [ ] Write tests with `@WithMockUser(roles = "ADMIN")` and another with `mutateWith(mockUser(...).roles("ADMIN"))` for `WebTestClient` |
| - [ ] Inspect the resulting filter chain at startup (turn on `DEBUG` for `org.springframework.security`) and read through the order |
