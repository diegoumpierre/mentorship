# HTTP Server (GET)

| Activities |
|------------|
| - [ ] Open a `ServerSocket` and accept connections in a loop — first version on a thread per connection just to feel the protocol |
| - [ ] Parse the request line (`GET /path HTTP/1.1`) and the headers; reject anything malformed with a clean `400` |
| - [ ] Build a tiny `Router` that maps a path (and a path pattern with `:id`) to a `Handler` |
| - [ ] Write a `Response` builder that handles status, headers, and body, and a `Request` view (path, query, headers) |
| - [ ] Serve text, JSON, and a small static file from disk — set `Content-Type` and `Content-Length` correctly |
| - [ ] Return `404` for unknown routes and `405` (with `Allow: GET`) for routes that exist but don't accept the method |
| - [ ] Honor `Connection: keep-alive` and pipeline a few requests on one connection |
| - [ ] Move from thread-per-connection to a thread pool, then to NIO with a `Selector` (or to virtual threads on JDK 21+) — measure the difference |
| - [ ] Add a write timeout and a read timeout; close idle connections |
| - [ ] Cap the request line and header size to bound memory per connection |
| - [ ] Plug in a tiny middleware chain (logging, request id, gzip) — Decorator pattern over `Handler` |
| - [ ] Tests with a real client: hit it with `HttpClient` (JDK 11+), `curl`, and `wrk` for throughput |
| - [ ] Compare your throughput to a hello-world Spring Boot endpoint and write down where the gap comes from |
