package android.meli.core.data.network

import java.io.IOException

/**
 * Represents the result of a network operation.
 */
sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    sealed class Error: NetworkResult<Nothing>() {
        data class HttpError(val code: Int, val message: String? = null) : Error()
        data class NetworkError(val exception: IOException) : Error()
        data class UnknownError(val exception: Throwable) : Error()
    }
}