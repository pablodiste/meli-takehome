package android.meli.core.domain.repositories

import android.meli.core.domain.DomainResult
import android.meli.core.domain.model.Article
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing articles.
 */
interface ArticlesRepository {

    /**
     * Searches for articles based on the provided query.
     *
     * @param query The search query string.
     * @return A flow of [PagingData] containing articles matching the query.
     */
    fun searchArticles(query: String): Flow<PagingData<Article>>

    /**
     * Retrieves a specific article by its ID.
     *
     * @param id The ID of the article to retrieve.
     * @return A [DomainResult] containing the article if found, or an error if not.
     */
    suspend fun getArticle(id: Int): DomainResult<Article>

}
