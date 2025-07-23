package android.meli.domain

import android.meli.core.domain.SearchArticlesUseCase
import android.meli.core.domain.model.Article
import android.meli.core.domain.repositories.ArticlesRepository
import androidx.paging.PagingData
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchArticlesUseCaseTest {

    private lateinit var repository: ArticlesRepository
    private lateinit var searchArticlesUseCase: SearchArticlesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        searchArticlesUseCase = SearchArticlesUseCase(repository)
    }

    @Test
    fun `returns paging data for search query`() = runTest {
        val testArticles = listOf(
            Article(
                id = 1,
                title = "Test Article 1",
                summary = "First test article",
                authors = "Author 1",
                updatedAt = "2024-01-01",
                publishedAt = "2024-01-01",
                imageUrl = "http://example.com/test1.jpg",
                url = "http://example.com/article1"
            ),
            Article(
                id = 2,
                title = "Test Article 2",
                summary = "Second test article",
                authors = "Author 2",
                updatedAt = "2024-01-02",
                publishedAt = "2024-01-01",
                imageUrl = "http://example.com/test2.jpg",
                url = "http://example.com/article2"
            )
        )
        val pagingData = PagingData.from(testArticles)
        every { repository.searchArticles("test query") } returns flowOf(pagingData)

        val result = searchArticlesUseCase("test query")
        val snapshot = result.first()

        assertEquals(pagingData, snapshot)
    }

    @Test
    fun `returns empty paging data for empty results`() = runTest {
        val emptyPagingData = PagingData.from(emptyList<Article>())
        every { repository.searchArticles("empty query") } returns flowOf(emptyPagingData)

        val result = searchArticlesUseCase("empty query")
        val snapshot = result.first()

        assertEquals(emptyPagingData, snapshot)
    }
}