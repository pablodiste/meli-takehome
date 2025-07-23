package android.meli.core.data.network.articles

import android.meli.core.data.network.NetworkDataSource
import android.meli.core.data.network.NetworkResult
import javax.inject.Inject

/**
 * Interface for fetching articles from the network.
 */
interface ArticleNetworkDataSource {
    suspend fun getArticles(query: String, limit: Int, offset: Int): NetworkResult<ArticleResponse>
}

class DefaultArticleNetworkDataSource @Inject constructor(
    val articlesService: ArticlesService
): NetworkDataSource(), ArticleNetworkDataSource {

    override suspend fun getArticles(query: String, limit: Int, offset: Int): NetworkResult<ArticleResponse> {
        return apiCall { articlesService.searchArticles(query, limit, offset, "-updated_at") }
    }

}
