package android.meli.core.data.network

import retrofit2.Response
import timber.log.Timber
import java.io.IOException

/**
 * Base class for network data sources.
 */
abstract class NetworkDataSource {

   protected suspend fun <T> apiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    NetworkResult.Success(body)
                } else {
                    Timber.e("Response body is null for ${response.raw()}")
                    NetworkResult.Error.UnknownError(NullPointerException("Response body is null"))
                }
            } else {
                Timber.e("HTTP error: ${response.code()} - ${response.errorBody()?.string()}")
                NetworkResult.Error.HttpError(response.code(), response.errorBody()?.string())
            }
        } catch (e: IOException) {
            Timber.e("Network error: ${e.message}")
            NetworkResult.Error.NetworkError(e)
        } catch (e: Exception) {
            Timber.e("Unknown error: ${e.message}")
            NetworkResult.Error.UnknownError(e)
        }
    }

}