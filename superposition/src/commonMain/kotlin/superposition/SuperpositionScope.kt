@file:Suppress("UNCHECKED_CAST")
@file:OptIn(SuperpositionInternalApi::class)

package superposition

class SuperpositionScope<F : Any> @PublishedApi internal constructor(
    @PublishedApi
    internal val unwrap: Superposition.Unwrap<F>
) {

    inline fun <S1, S2> Sp<S1, F>.map(body: (S1) -> S2): Sp<S2, F> = peek(onSuccess = {
        return superposition(unwrap) {
            body(
                it
            )
        }
    }) as Sp<S2, F>

    inline fun <S1, S2> Sp<S1, F>.flatMap(body: (S1) -> Sp<S2, F>): Sp<S2, F> = peek(onSuccess = {
        return superposition(unwrap) {
            return body(it)
        } as Sp<S2, F>
    }) as Sp<S2, F>

    inline fun <S> Sp<S, F>.onSuccess(body: (S) -> Unit) =
        peek(onSuccess = body)

    inline fun <S> Sp<S, F>.onFailure(body: (F) -> Unit) =
        peek(onFailure = body)

}
