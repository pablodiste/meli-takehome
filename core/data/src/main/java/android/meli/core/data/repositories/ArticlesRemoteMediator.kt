package android.meli.core.data.repositories

import android.meli.core.data.local.database.ArticleDatabaseDataSource
import android.meli.core.data.local.database.ArticleEntity
import android.meli.core.data.mappers.toEntity
import android.meli.core.data.network.NetworkResult
import android.meli.core.data.network.articles.ArticleNetworkDataSource
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import timber.log.Timber

/**
 * RemoteMediator for loading articles from a network source into a local database.
 */
@OptIn(ExperimentalPagingApi::class)
class ArticlesRemoteMediator(
    private val query: String,
    private val articleNetworkDataSource: ArticleNetworkDataSource,
    private val articleDatabaseDataSource: ArticleDatabaseDataSource,
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {

        Timber.tag("Mediator").d("$loadType")

        val offset = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> articleDatabaseDataSource.getArticleCountForQuery(query)
                .takeIf { it > 0 } ?: return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            Timber.tag("Mediator").d("getArticles: query=$query, limit=${state.config.pageSize}, offset=$offset")
            val response = articleNetworkDataSource.getArticles(query, state.config.pageSize, offset)
            val data = when (response) {
                is NetworkResult.Success -> response.data
                is NetworkResult.Error.HttpError -> return MediatorResult.Error(RuntimeException("HTTP Error: ${response.code}, Message: ${response.message}"))
                is NetworkResult.Error.NetworkError -> return MediatorResult.Error(response.exception)
                is NetworkResult.Error.UnknownError -> return MediatorResult.Error(response.exception)
            }
            if (loadType == LoadType.REFRESH) articleDatabaseDataSource.clearArticlesForQuery(query)
            articleDatabaseDataSource.insertAll(data.results.map { it.toEntity(query) })
            Timber.tag("Mediator").d("endOfPaginationReached ${data.next == null}")
            MediatorResult.Success(endOfPaginationReached = data.next == null)
        } catch (e: Exception) {
            Timber.tag("Mediator").e("Error loading articles: ${e.message}")
            MediatorResult.Error(e)
        }
    }
}
