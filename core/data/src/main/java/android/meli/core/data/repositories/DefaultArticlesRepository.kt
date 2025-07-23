package android.meli.core.data.repositories

import android.meli.core.data.local.database.ArticleDatabaseDataSource
import android.meli.core.data.local.database.DatabaseResult
import android.meli.core.data.mappers.toDomain
import android.meli.core.data.network.articles.ArticleNetworkDataSource
import android.meli.core.domain.DomainResult
import android.meli.core.domain.model.Article
import android.meli.core.domain.repositories.ArticlesRepository
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Default implementation of [ArticlesRepository] that uses a local database and a network data source.
 */
@OptIn(ExperimentalPagingApi::class)
class DefaultArticlesRepository @Inject constructor(
    private val articleDatabaseDataSource: ArticleDatabaseDataSource,
    private val articleNetworkDataSource: ArticleNetworkDataSource,
) : ArticlesRepository {

    override fun searchArticles(query: String): Flow<PagingData<Article>> {
        val pagingSourceFactory = { articleDatabaseDataSource.getArticlesForQuery(query) }
        return Pager(
            config = PagingConfig(pageSize = 10),
            remoteMediator = ArticlesRemoteMediator(query, articleNetworkDataSource, articleDatabaseDataSource),
            pagingSourceFactory = pagingSourceFactory
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() } // Map Room entity → domain model
        }
    }

    override suspend fun getArticle(id: Int): DomainResult<Article> {
        val result = articleDatabaseDataSource.getArticle(id)
        return when (result) {
            is DatabaseResult.Success -> DomainResult.Success(result.data.toDomain())
            is DatabaseResult.NotFound -> DomainResult.Failure(DomainResult.FailureReason.NotFound)
            is DatabaseResult.Error -> DomainResult.Failure(DomainResult.FailureReason.DatabaseError)
        }
    }

}
