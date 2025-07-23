package android.meli.core.domain

/**
 * A sealed class representing the result of a domain operation.
 */
sealed class DomainResult<out R> {
    data class Success<out T>(val data: T) : DomainResult<T>()
    data class Failure(val reason: FailureReason) : DomainResult<Nothing>()

    sealed class FailureReason {
        object NoConnection : FailureReason()
        object NotFound : FailureReason()
        object Unauthorized : FailureReason()
        object NetworkError : FailureReason()
        object DatabaseError : FailureReason()
        object ServerError : FailureReason()
        object Unknown : FailureReason()
    }
}

fun <T> DomainResult<T>.successOr(fallback: T): T {
    return (this as? DomainResult.Success<T>)?.data ?: fallback
}

