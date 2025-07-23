package android.meli.core.data.network

import android.meli.core.data.network.articles.ArticlesService
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient is a singleton class that provides a configured Retrofit instance
 * for making network requests to the API.
 */
class RetrofitClient() {

    private val BASE_URL = "https://api.spaceflightnewsapi.net/v4/"

    // Lazy initialization for OkHttpClient
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            // Add Logging Interceptor for debugging (remove in release builds or use BuildConfig.DEBUG)
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            addInterceptor(loggingInterceptor)
            // Connection Timeouts
            connectTimeout(30, TimeUnit.SECONDS)
            readTimeout(30, TimeUnit.SECONDS)
            writeTimeout(30, TimeUnit.SECONDS)
        }.build()
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    // Lazy initialization for Retrofit instance
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Use the configured OkHttpClient
            .addConverterFactory(
                json.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()
                )
            )
            // Add other call adapter factories if needed (e.g., for RxJava or custom ones)
            .build()
    }

    /**
     * Creates an instance of the Retrofit service.
     * @param serviceClass The service interface class (e.g., YourApiService::class.java)
     * @return An instance of the service.
     */
    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)
    }

    val articlesService: ArticlesService by lazy {
        retrofit.create(ArticlesService::class.java)
    }

}