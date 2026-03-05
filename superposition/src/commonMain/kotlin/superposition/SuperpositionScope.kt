@file:Suppress("UNCHECKED_CAST")
@file:OptIn(SuperpositionInternalApi::class)

package superposition

public class SuperpositionScope<F : Any> @PublishedApi internal constructor(
    @PublishedApi
    internal val unwrap: Superposition.Unwrap<F>
) {

    public inline fun <S1, S2> Sp<S1, F>.map(body: (S1) -> S2): Sp<S2, F> = peek(onSuccess = {
        return superposition(unwrap) {
            body(
                it
            )
        }
    }) as Sp<S2, F>

    public inline fun <S1, S2> Sp<S1, F>.flatMap(body: (S1) -> Sp<S2, F>): Sp<S2, F> = peek(onSuccess = {
        return superposition(unwrap) {
            return body(it)
        } as Sp<S2, F>
    }) as Sp<S2, F>

    public inline fun <S> Sp<S, F>.onSuccess(body: (S) -> Unit): Sp<S, F> =
        peek(onSuccess = body)

    public inline fun <S> Sp<S, F>.onFailure(body: (F) -> Unit): Sp<S, F> =
        peek(onFailure = body)

}
