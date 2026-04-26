# Spring Security: CSRF Protection

| Activities |
|------------|
| - [ ] Stand up a basic Spring Security setup and observe that CSRF protection is on by default for state-changing requests |
| - [ ] Hit a `POST` endpoint without a CSRF token and watch it get rejected with 403 |
| - [ ] Render the token in a Thymeleaf form (the default `<form>` integration adds it automatically) and post successfully |
| - [ ] Switch the token repository to `CookieCsrfTokenRepository.withHttpOnlyFalse()` for SPA flows and explain why |
| - [ ] Read the cookie from the SPA, send it back as the `X-XSRF-TOKEN` header, and confirm the request goes through |
| - [ ] Disable CSRF on a specific path with `csrf.ignoringRequestMatchers(...)` — and document why you'd ever do that (typically a stateless API behind another auth scheme) |
| - [ ] Demonstrate why CSRF is irrelevant for a pure stateless API authenticated only via `Authorization: Bearer ...` |
| - [ ] Try the deferred-token approach (`CsrfTokenRequestAttributeHandler`) introduced in Spring Security 6 |
| - [ ] Test a CSRF-protected endpoint with `MockMvc` using `with(csrf())` and again with `WebTestClient` using `mutateWith(csrf())` |
| - [ ] Reproduce a real cross-origin attack scenario (toy HTML page hitting your endpoint) and show CSRF blocking it |
