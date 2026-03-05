package superposition

abstract class UnhandledException(open val throwable: Throwable) {
    override fun toString(): String {
        return throwable.stackTraceToString()
    }
}