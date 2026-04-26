# Mockito

| Activities |
|------------|
| - [ ] Create a mock both ways: `Mockito.mock(SomeClass.class)` and `@Mock` with `MockitoExtension` |
| - [ ] Stub return values with `when(...).thenReturn(...)`, including the variant with several consecutive calls |
| - [ ] Use a custom `Answer` when you need the response to depend on the arguments |
| - [ ] Stub exceptions for both regular and void methods (`thenThrow` and `doThrow`) |
| - [ ] Verify behaviour with `verify`, `times`, `never`, `atLeast`, `atMost` |
| - [ ] Make sure nothing else happened with `verifyNoInteractions` and `verifyNoMoreInteractions` |
| - [ ] Pick the right matcher for the job: `any`, `eq`, `argThat` — and don't mix matchers with raw values by accident |
| - [ ] Capture an argument with `ArgumentCaptor` (or `@Captor`) and assert against the captured value |
| - [ ] Wrap a real instance with `@Spy` and stub a single method while the rest stays real |
| - [ ] Wire dependencies into a SUT with `@InjectMocks` (constructor injection first, fields as a fallback) |
| - [ ] Mock a static method with `Mockito.mockStatic` inside a try-with-resources block |
| - [ ] Enable `mockito-inline` and mock a final class or a final method |
| - [ ] Try the BDD aliases: `given(...).willReturn(...)` and `then(...).should(...)` |
| - [ ] Reach for `reset` only when there's no cleaner option — and write down why |
