# Spring Boot - Rest Support, RestTemplate

| Activities |
|------------|
| - [ ] Build a `@RestController` with the four common verbs: GET, POST, PUT, DELETE |
| - [ ] Bind a path variable, a query parameter, a request body, a header — one of each |
| - [ ] Validate the body with `@Valid` plus Jakarta Validation annotations and inspect the resulting error response |
| - [ ] Return a `ResponseEntity` to control status code and headers explicitly |
| - [ ] Centralize error handling with `@RestControllerAdvice` and a couple of `@ExceptionHandler`s |
| - [ ] Adopt RFC 7807 `ProblemDetail` for error responses |
| - [ ] On the client side, configure a `RestTemplate` bean (timeouts, interceptors, message converters) |
| - [ ] Call all four verbs with `RestTemplate`: `getForObject`, `postForEntity`, `exchange`, `delete` |
| - [ ] Send custom headers and consume a response wrapped in `ResponseEntity<T>` |
| - [ ] Handle non-2xx responses with a `ResponseErrorHandler` |
| - [ ] Migrate the same client code to `RestClient` (Spring 6.1+) and notice the cleaner fluent API |
| - [ ] Add a tiny `ClientHttpRequestInterceptor` for logging or auth header injection |
