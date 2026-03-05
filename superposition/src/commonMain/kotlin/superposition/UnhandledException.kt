package superposition

public abstract class UnhandledException(public open val throwable: Throwable) {
    override fun toString(): String {
        return throwable.stackTraceToString()
    }
}