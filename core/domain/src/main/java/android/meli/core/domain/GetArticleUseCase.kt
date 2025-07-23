package android.meli.core.domain

import android.meli.core.domain.model.Article
import android.meli.core.domain.repositories.ArticlesRepository
import javax.inject.Inject

/**
 * Use case for retrieving a single article by its ID.
 */
class GetArticleUseCase @Inject constructor(
    private val articlesRepository: ArticlesRepository,
) {
    suspend operator fun invoke(id: Int): DomainResult<Article> = articlesRepository.getArticle(id)
}