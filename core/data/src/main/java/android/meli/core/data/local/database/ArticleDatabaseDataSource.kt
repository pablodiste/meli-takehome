package android.meli.core.data.local.database

import androidx.paging.PagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

interface ArticleDatabaseDataSource {
    fun getArticlesForQuery(query: String): PagingSource<Int, ArticleEntity>
    suspend fun getArticleCountForQuery(query: String): Int
    suspend fun insertAll(articles: List<ArticleEntity>)
    suspend fun clearArticlesForQuery(query: String)
    suspend fun getArticle(id: Int): DatabaseResult<ArticleEntity>
}

class DefaultArticleDatabaseDataSource @Inject constructor(
    val articleDao: ArticleDao
): ArticleDatabaseDataSource {

    override fun getArticlesForQuery(query: String): PagingSource<Int, ArticleEntity> {
        return articleDao.getArticlesForQuery(query)
    }

    override suspend fun getArticleCountForQuery(query: String): Int = withContext(Dispatchers.IO) {
        return@withContext articleDao.getArticleCountForQuery(query)
    }

    override suspend fun insertAll(articles: List<ArticleEntity>) = withContext(Dispatchers.IO) {
        return@withContext articleDao.insertAll(articles)
    }

    override suspend fun clearArticlesForQuery(query: String) = withContext(Dispatchers.IO) {
        return@withContext articleDao.clearArticlesForQuery(query)
    }

    override suspend fun getArticle(id: Int): DatabaseResult<ArticleEntity> = withContext(Dispatchers.IO) {
        return@withContext try {
            val article = articleDao.getArticle(id)
            if (article != null) {
                DatabaseResult.Success(article)
            } else {
                DatabaseResult.NotFound
            }
        } catch (e: Exception) {
            Timber.e("Error fetching article with id $id: ${e.message}")
            DatabaseResult.Error(e)
        }
    }
}
