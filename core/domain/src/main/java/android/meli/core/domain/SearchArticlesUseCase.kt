package android.meli.core.domain

import android.meli.core.domain.model.Article
import android.meli.core.domain.repositories.ArticlesRepository
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for fetching articles
 */
class SearchArticlesUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> = articlesRepository.searchArticles(query)
}
