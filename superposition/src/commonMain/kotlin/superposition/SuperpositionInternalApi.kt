package superposition

@RequiresOptIn(
    message = "This internal API must not be used in production code. It's bypassing the fundamental rules set by the API surface and therefore is strictly forbidden to use.",
    level = RequiresOptIn.Level.ERROR
)
annotation class SuperpositionInternalApi