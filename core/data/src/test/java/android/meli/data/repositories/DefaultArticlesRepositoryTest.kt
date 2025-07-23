package android.meli.data.repositories

import android.meli.core.data.local.database.ArticleDatabaseDataSource
import android.meli.core.data.local.database.ArticleEntity
import android.meli.core.data.local.database.DatabaseResult
import android.meli.core.data.network.NetworkResult
import android.meli.core.data.network.articles.ArticleNetwork
import android.meli.core.data.network.articles.ArticleNetworkDataSource
import android.meli.core.data.network.articles.ArticleResponse
import android.meli.core.data.network.articles.AuthorNetwork
import android.meli.core.data.repositories.DefaultArticlesRepository
import android.meli.core.domain.DomainResult
import androidx.paging.PagingSource
import androidx.paging.PagingState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultArticlesRepositoryTest {

    private lateinit var articleDatabaseDataSource: ArticleDatabaseDataSource
    private lateinit var articleNetworkDataSource: ArticleNetworkDataSource
    private lateinit var repository: DefaultArticlesRepository

    @get:Rule
    val mainDispatcherRule = StandardDispatcherRule()

    @Before
    fun setUp() {
        articleDatabaseDataSource = mockk()
        articleNetworkDataSource = mockk()
        repository = DefaultArticlesRepository(articleDatabaseDataSource, articleNetworkDataSource)
    }

    @Test
    fun `searchArticles returns paging data from database`() = runTest {
        val articleEntities = listOf(
            ArticleEntity(
                id = 1,
                title = "Test Article 1",
                summary = "Summary 1",
                authors = "Author 1",
                updatedAt = "2024-01-01",
                publishedAt = "2024-01-01",
                imageUrl = "http://example.com/image1.jpg",
                url = "http://example.com/article1",
                searchQuery = "test"
            ),
            ArticleEntity(
                id = 2,
                title = "Test Article 2",
                summary = "Summary 2",
                authors = "Author 2",
                updatedAt = "2024-01-02",
                publishedAt = "2024-01-01",
                imageUrl = "http://example.com/image2.jpg",
                url = "http://example.com/article2",
                searchQuery = "test"
            )
        )

        val articlesNetwork = ArticleResponse(
            count = 2,
            next = "http://api.example.com/articles?page=2",
            previous = null,
            results = listOf(
                ArticleNetwork(
                    id = 1,
                    title = "Test Article 1",
                    summary = "Summary 1",
                    image_url = "http://example.com/image1.jpg",
                    published_at = "2024-01-01",
                    updated_at = "2024-01-01",
                    url = "http://example.com/article1",
                    authors = listOf(
                        AuthorNetwork(name = "Author 1")
                    )
                ),
                ArticleNetwork(
                    id = 2,
                    title = "Test Article 2",
                    summary = "Summary 2",
                    image_url = "http://example.com/image2.jpg",
                    published_at = "2024-01-01",
                    updated_at = "2024-01-02",
                    url = "http://example.com/article2",
                    authors = listOf(
                        AuthorNetwork(name = "Author 2")
                    )
                )
            )
        )

        val fakePagingSource = FakeArticlePagingSource(articleEntities)
        every { articleDatabaseDataSource.getArticlesForQuery("test") } returns fakePagingSource
        coEvery { articleNetworkDataSource.getArticles("test", 0, 10) } returns NetworkResult.Success(articlesNetwork)

        // Test the PagingSource directly
        val loadParams = PagingSource.LoadParams.Refresh(key = 1, loadSize = 10, placeholdersEnabled = false)
        val loadResult = fakePagingSource.load(loadParams)
        assertTrue(loadResult is PagingSource.LoadResult.Page)

        val pageResult = loadResult as PagingSource.LoadResult.Page
        assertEquals(articleEntities, pageResult.data)

        // Verify the repository calls the correct data source
        repository.searchArticles("test").first()
        coVerify {
            articleDatabaseDataSource.getArticlesForQuery("test")
        }
    }

    @Test
    fun `getArticle returns success when database returns success`() = runTest {
        val articleEntity = ArticleEntity(
            id = 1,
            title = "Test Article",
            summary = "Test Summary",
            authors = "Test Author",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            imageUrl = "http://example.com/image.jpg",
            url = "http://example.com/article",
            searchQuery = "test"
        )
        val databaseResult = DatabaseResult.Success(articleEntity)
        coEvery { articleDatabaseDataSource.getArticle(1) } returns databaseResult

        val result = repository.getArticle(1)

        assertTrue(result is DomainResult.Success)
        assertEquals("Test Article", (result as DomainResult.Success).data.title)
        assertEquals(1, result.data.id)
        coVerify { articleDatabaseDataSource.getArticle(1) }
    }

    @Test
    fun `getArticle returns not found when database returns not found`() = runTest {
        val databaseResult = DatabaseResult.NotFound
        coEvery { articleDatabaseDataSource.getArticle(1) } returns databaseResult

        val result = repository.getArticle(1)

        assertTrue(result is DomainResult.Failure)
        assertEquals(DomainResult.FailureReason.NotFound, (result as DomainResult.Failure).reason)
        coVerify { articleDatabaseDataSource.getArticle(1) }
    }

    @Test
    fun `getArticle returns database error when database returns error`() = runTest {
        val databaseResult = DatabaseResult.Error(RuntimeException("Database error"))
        coEvery { articleDatabaseDataSource.getArticle(1) } returns databaseResult

        val result = repository.getArticle(1)

        assertTrue(result is DomainResult.Failure)
        assertEquals(DomainResult.FailureReason.DatabaseError, (result as DomainResult.Failure).reason)
        coVerify { articleDatabaseDataSource.getArticle(1) }
    }
}

class FakeArticlePagingSource(private val data: List<ArticleEntity>) : PagingSource<Int, ArticleEntity>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleEntity> {
        val page = params.key ?: 1
        val pageSize = params.loadSize
        val start = (page - 1) * pageSize
        val end = minOf(start + pageSize, data.size)
        val subList = data.subList(start, end)
        return LoadResult.Page(
            data = subList,
            prevKey = if (page == 1) null else page - 1,
            nextKey = if (end >= data.size) null else page + 1
        )
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleEntity>): Int? = 1
}

