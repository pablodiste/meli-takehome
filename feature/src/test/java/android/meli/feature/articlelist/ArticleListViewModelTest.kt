package android.meli.feature.articlelist

import android.meli.core.domain.SearchArticlesUseCase
import android.meli.core.domain.model.Article
import android.meli.feature.StandardDispatcherRule
import androidx.paging.PagingData
import app.cash.turbine.test
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleListViewModelTest {

    private lateinit var searchArticlesUseCase: SearchArticlesUseCase
    private lateinit var viewModel: ArticleListViewModel

    @get:Rule
    val mainDispatcherRule = StandardDispatcherRule()

    private val testArticles = listOf(
        Article(
            id = 1,
            title = "Test Article 1",
            summary = "Summary 1",
            authors = "Author 1",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            imageUrl = "http://example.com/image1.jpg",
            url = "http://example.com/article1"
        ),
        Article(
            id = 2,
            title = "Test Article 2",
            summary = "Summary 2",
            authors = "Author 2",
            updatedAt = "2024-01-02",
            publishedAt = "2024-01-02",
            imageUrl = "http://example.com/image2.jpg",
            url = "http://example.com/article2"
        )
    )

    @Before
    fun setUp() {
        searchArticlesUseCase = mockk()
        every { searchArticlesUseCase(any()) } returns flowOf(PagingData.from(testArticles))
        viewModel = ArticleListViewModel(searchArticlesUseCase)
    }

    @Test
    fun `initial search query is empty`() = runTest {
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `updateSearchQuery updates search query state`() = runTest {
        viewModel.updateSearchQuery("test query")
        assertEquals("test query", viewModel.searchQuery.value)
    }

    @Test
    fun `search updates search query state`() = runTest {
        viewModel.search("search text")
        assertEquals("search text", viewModel.searchQuery.value)
    }

    @Test
    @Suppress("UnusedFlow")
    fun `uiState triggers search use case with debounced query`() = runTest {
        every { searchArticlesUseCase("test") } returns flowOf(PagingData.from(testArticles))

        viewModel.updateSearchQuery("test")
        advanceTimeBy(300) // Wait for debounce
        advanceUntilIdle()

        verify {
            searchArticlesUseCase("test")
        }
    }

    @Test
    @Suppress("UnusedFlow")
    fun `uiState debounces rapid query changes`() = runTest {
        every { searchArticlesUseCase("final") } returns flowOf(PagingData.from(testArticles))

        viewModel.updateSearchQuery("f")
        viewModel.updateSearchQuery("fi")
        viewModel.updateSearchQuery("fin")
        viewModel.updateSearchQuery("fina")
        viewModel.updateSearchQuery("final")

        advanceTimeBy(300) // Wait for debounce
        advanceUntilIdle()

        verify(exactly = 1) {
            searchArticlesUseCase("final")
        }
    }

    @Test
    fun `uiState returns paging data from use case`() = runTest {
        every { searchArticlesUseCase("test") } returns flowOf(PagingData.from(testArticles))

        viewModel.updateSearchQuery("test")
        advanceTimeBy(300) // Wait for debounce
        advanceUntilIdle()

        verify { searchArticlesUseCase("test") }

        val pagingData = viewModel.uiState.first()
        assert(pagingData != null) // Ensure paging data is not null
    }

    @Test
    fun `articleSelected emits navigation event`() = runTest {
        val article = testArticles.first()

        viewModel.uiEvents.test {
            advanceUntilIdle() // Ensure initial state is emitted

            viewModel.articleSelected(article)
            advanceUntilIdle()

            val event = awaitItem()
            assertTrue(event is ArticleListUiEvent.NavigateToDetails)
            assertEquals(article.id, (event as ArticleListUiEvent.NavigateToDetails).id)
        }
    }

    @Test
    fun `distinctUntilChanged prevents duplicate queries`() = runTest {
        every { searchArticlesUseCase("same") } returns flowOf(PagingData.from(testArticles))

        viewModel.updateSearchQuery("same")
        advanceTimeBy(300)
        advanceUntilIdle()

        viewModel.updateSearchQuery("same")
        advanceTimeBy(300)
        advanceUntilIdle()

        verify(exactly = 1) { searchArticlesUseCase("same") }
    }
}
