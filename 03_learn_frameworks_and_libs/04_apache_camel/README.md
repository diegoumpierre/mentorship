# Apache Camel

| Activities |
|------------|
| - [ ] Spin up a `CamelContext` (standalone main, then with the Spring Boot starter) and add a `RouteBuilder` |
| - [ ] Build a tiny `from(...).to(...)` route that copies files between two folders |
| - [ ] Use the timer component to fire a route every N seconds |
| - [ ] Wire HTTP in: expose a route with `platform-http` (or `jetty`) and call out with `http` |
| - [ ] Send and consume from Kafka with `kafka:topicName` |
| - [ ] Send and consume from a JMS broker (ActiveMQ or Artemis) |
| - [ ] Apply EIPs explicitly: `choice().when().otherwise()` for content-based routing |
| - [ ] Try `split` + `aggregate` over a list payload and join the pieces back with a strategy |
| - [ ] Use `multicast` to fan out the same exchange to several endpoints |
| - [ ] Add error handling: `errorHandler(deadLetterChannel(...))` and `onException(...).maximumRedeliveries(...)` |
| - [ ] Convert formats with `marshal().json(JsonLibrary.Jackson)` and `unmarshal()` |
| - [ ] Write a custom `Processor` for the bits the DSL doesn't cover |
| - [ ] Test a route with `CamelTestSupport` and `MockEndpoint` assertions |
| - [ ] Expose metrics through Micrometer and skim what Camel emits per route |
