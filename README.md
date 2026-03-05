# Superposition

**Superposition** is a lightweight, high-performance `Result` type for Kotlin. It leverages Kotlin's `value class` to
minimize object allocation overhead while providing a robust, functional approach to strictly typed error handling.

## Features

* 🚀 **Zero Overhead**: Implemented as a `@JvmInline value class`.
* 🔒 **Type-Safe Failures**: Define arbitrary Failure domain objects, not limited to `Throwable`.
* 🛑 **Explicit Handling**: The API enforces handling failure cases before accessing success values.
* 🔗 **Functional Composition**: Includes standard operators like `map` and `recover`.
* 🍬 **Syntactic Sugar**: Operator overloading for concise syntax.

## Usage

### 1. The Core Concept

The core type is `Superposition<Success, Failure>`. To access the `Success` value, you must use `handle`. The
`onFailure` block returns `Nothing`, guaranteeing flow interruption on error.

```kotlin
fun processData(result: Superposition<String, ApiError>): String {
    // Access is only possible via handle
    val value = result.handle { error ->
        // Must return Nothing (throw, return, etc.)
        println("Error: ${error.code}")
        return "Early exit" 
    }
    
    // 'value' is guaranteed to be String here
    return value
}
```

### 2. Creating Superpositions

Wrap throwing code using the `superposition` builder and an `Unwrap` strategy to map exceptions to domain failures.

```kotlin
sealed interface MyDomainError {
    data object DomainRuleBreak1 : MyDomainError
    class Unknown(error: Throwable) : UnhandledException(error), MyDomainError
    companion object : Superposition.Unwrap<MyDomainError> {
        override fun map(error: Throwable) = when(error) {
            is RuntimeException -> DomainRuleBreak1
            else -> Unknown
        }    
    }
}

val result: Superposition<String, MyDomainError> = superposition(MyDomainError) {
    if (someCondition) throw RuntimeException("Boom")
    "Success Data"
}
```

### 3. Concise Syntax

**The `invoke` operator**  
Shortcut for `handle`:

```kotlin
val value = result { error -> return "Failed" }
```

**Guaranteed Success**  
If the Failure type is `Nothing`, you can unwrap directly:
```kotlin
val successOnly: Superposition<String, Nothing> = ...
val value = successOnly() // No lambda required
```

### 4. Transformation & Recovery

**Map**  
Transform success values while preserving failure types:
```kotlin
val intResult = result.map { str -> str.length }
```

**Flat Map**  
Transform success values while preserving failure types:
```kotlin
val fooBar: (String) -> Superposition<Int, ...> = { ... }
val result = result.flatMap { str -> fooBar(str) }
```

**Recover**  
Provide a fallback value instead of interrupting flow:
```kotlin
val value = result.recover { failure -> "Fallback: ${failure.msg}" }
```

### 5. Composition

Chain operations using the `superposition` scope.

**Sequential unwrap**
```kotlin
val result = superposition(MyDomainError) {
    // Unwrap results sequentially
    val user = getUser().handle { throw UserNotFound(it) }
    val service = getLoadBalancedService().handle { throw ServiceUnavailable(it) }
    getProfile(user, service) // Returns the final result
}
```

**Flattened unwrap**
```kotlin
val result = flatSuperposition(MyDomainError) {
    val user = getUser().handle { throw UserNotFound(it) }
    getProfile(user) // assumes -> getProfile(): Superposition<??, MyDomainError> 
}
```

### 6. Debugging

The library includes `UnhandledException` which prints stack traces to `System.err` for unhandled exceptions during
creation or mapping, aiding in development debugging.