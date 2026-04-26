# Google gRPC

| Activities |
|------------|
| - [ ] Write a `.proto` with a service and a couple of messages, then wire `protoc` (or the Maven/Gradle plugin) to generate stubs |
| - [ ] Implement the four call styles, one per RPC: unary, server-streaming, client-streaming, bidirectional streaming |
| - [ ] Stand up a `ServerBuilder` server and call it with both blocking and async stubs |
| - [ ] Pass a deadline on the client and provoke a `DEADLINE_EXCEEDED` |
| - [ ] Cancel an in-flight call from the client and observe it on the server |
| - [ ] Send metadata both ways (auth header, request id) using `Metadata` and a header interceptor |
| - [ ] Map domain errors to `Status.INVALID_ARGUMENT`, `NOT_FOUND`, `FAILED_PRECONDITION` instead of leaking exceptions |
| - [ ] Wire TLS on the server and trust it on the client |
| - [ ] Add a `ClientInterceptor` and a `ServerInterceptor` for logging and timing |
| - [ ] Stand up `grpc-health-probe` style health checks via the standard health service |
| - [ ] Turn reflection on so `grpcurl` can introspect the server |
| - [ ] Test the service in-process with `InProcessServerBuilder` |
| - [ ] Compare a gRPC call with the equivalent REST call on the same model — payload size, latency, ergonomics |
