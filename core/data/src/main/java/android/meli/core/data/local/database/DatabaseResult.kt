package android.meli.core.data.local.database

sealed class DatabaseResult<out T> {
    data class Success<out T>(val data: T) : DatabaseResult<T>()
    object NotFound : DatabaseResult<Nothing>()
    data class Error(val throwable: Throwable): DatabaseResult<Nothing>()
}