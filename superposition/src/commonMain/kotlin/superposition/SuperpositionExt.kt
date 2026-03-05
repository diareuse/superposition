@file:Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")

package superposition

// --- RECOVERY

public inline fun <S, F : Any> Sp<S, F>.recover(
    body: (F) -> S
): S = handle { return body(it) }

public inline fun <S1, S2, F : Any> Superposition<S1, F>.handle(
    onFailure: (F) -> S2,
    onSuccess: (S1) -> S2
): S2 = onSuccess(handle { return onFailure(it) })

// --- ALTERNATIVE ACCESS

// val value = superposition<String, Nothing> { "ok" }.handle()
public inline fun <S> Superposition<S, Nothing>.handle(): S = handle { it }

// val (value) = superposition<String, Nothing> { "ok" }
public inline operator fun <S> Superposition<S, Nothing>.component1(): S = handle()

// val result: Superposition<String, Any> = superposition { "ok" }
// val value = result { error(it) }
public inline operator fun <S, F : Any> Superposition<S, F>.invoke(onFailure: (F) -> Nothing): S =
    handle(onFailure)

// val result: Superposition<String, Nothing> = superposition { "ok" }
// val value = result()
public inline operator fun <S> Superposition<S, Nothing>.invoke(): S = handle()

// --- WRAP

@OptIn(SuperpositionInternalApi::class)
public fun <S, F : Any> Superposition.Companion.success(value: S): Superposition<S, F> =
    Superposition.success(value) as Sp<S, F>

@OptIn(SuperpositionInternalApi::class)
public fun <S, F : Any> Superposition.Companion.failure(value: F): Superposition<S, F> =
    Superposition.failure(value) as Sp<S, F>