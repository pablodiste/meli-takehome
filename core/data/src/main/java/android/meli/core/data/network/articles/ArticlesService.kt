package android.meli.core.data.network.articles

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticlesService {

    /**
     * Retrieves a list of articles.
     */
    @GET("articles/")
    suspend fun searchArticles(
        @Query("search") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("ordering") ordering: String,
    ): Response<ArticleResponse>

}