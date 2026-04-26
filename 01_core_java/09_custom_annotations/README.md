# Custom Annotations

| Activities |
|------------|
| - [ ] Declare an annotation with `@interface`, give it elements with default values, and add an array element and an enum element |
| - [ ] Mark it with `@Retention` and walk through what `SOURCE`, `CLASS` and `RUNTIME` actually mean by checking which ones reflection can see |
| - [ ] Restrict where it can be applied with `@Target` (try `TYPE`, `METHOD`, `FIELD`, `PARAMETER`) |
| - [ ] Read the annotation back at runtime with `getAnnotation` on a class, a method and a field |
| - [ ] List everything on a class with `getAnnotations` |
| - [ ] Build a tiny "framework": scan a package, find classes carrying your annotation, then invoke methods that carry another |
| - [ ] Make it `@Repeatable` and read the values back with `getAnnotationsByType` |
| - [ ] Mark one as `@Inherited` and prove a subclass picks it up — and that the same trick doesn't work on methods |
| - [ ] Compose a higher-level annotation out of existing ones (meta-annotation pattern) |
