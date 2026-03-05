@file:Suppress("UNCHECKED_CAST")
@file:OptIn(SuperpositionInternalApi::class)

package superposition

import kotlin.jvm.JvmInline

typealias Sp<Success, Failure> = Superposition<Success, Failure>

@JvmInline
value class Superposition<Success, Failure : Any> private constructor(
    @PublishedApi
    internal val value: Any?
) {

    inline fun handle(onFailure: (Failure) -> Nothing): Success {
        when (value) {
            is Error -> onFailure(value.value as Failure)
            else -> return value as Success
        }
    }

    @SuperpositionInternalApi
    @PublishedApi
    internal inline fun peek(
        onFailure: (Failure) -> Unit = {},
        onSuccess: (Success) -> Unit = {}
    ): Sp<Success, Failure> = apply {
        when (value) {
            is Error -> onFailure(value.value as Failure)
            else -> onSuccess(value as Success)
        }
    }

    @PublishedApi
    internal class Error(val value: Any)

    interface Unwrap<F> {
        fun map(error: Throwable): F
    }

    companion object {

        @SuperpositionInternalApi
        @PublishedApi
        internal fun <S> success(value: S): Sp<S, Nothing> = Superposition(value)

        @SuperpositionInternalApi
        @PublishedApi
        internal fun <F : Any> failure(value: F): Sp<Nothing, F> = Superposition(Error(value))
    }

}

@Suppress("UNCHECKED_CAST")
inline fun <S, F : Any> superposition(
    unwrap: Superposition.Unwrap<F>,
    block: SuperpositionScope<F>.() -> S
) = try {
    Sp.success(block(SuperpositionScope(unwrap)))
} catch (e: Throwable) {
    Sp.failure(unwrap.map(e))
} as Sp<S, F>

@Suppress("UNCHECKED_CAST")
inline fun <S, F : Any> flatSuperposition(
    unwrap: Superposition.Unwrap<F>,
    block: SuperpositionScope<F>.() -> Sp<S, F>
) = try {
    block(SuperpositionScope(unwrap))
} catch (e: Throwable) {
    Sp.failure(unwrap.map(e))
} as Sp<S, F>