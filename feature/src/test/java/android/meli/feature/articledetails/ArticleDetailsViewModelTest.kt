package android.meli.feature.articledetails

import android.meli.core.domain.DomainResult
import android.meli.core.domain.GetArticleUseCase
import android.meli.core.domain.model.Article
import android.meli.feature.R
import android.meli.feature.StandardDispatcherRule
import android.net.Uri
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailsViewModelTest {

    private lateinit var getArticleUseCase: GetArticleUseCase
    private lateinit var viewModel: ArticleDetailsViewModel

    @get:Rule
    val mainDispatcherRule = StandardDispatcherRule()

    @Before
    fun setUp() {
        getArticleUseCase = mockk()
        viewModel = ArticleDetailsViewModel(getArticleUseCase)
    }

    @Test
    fun `initial state is loading`() = runTest {
        val initialState = viewModel.uiState.value
        assertTrue(initialState is ArticleDetailsUiState.Loading)
    }

    @Test
    fun `fetchArticle sets success state when use case returns success`() = runTest {
        val article = Article(
            id = 1,
            title = "Test Article",
            summary = "Test Summary",
            authors = "Test Author",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            imageUrl = "http://example.com/image.jpg",
            url = "http://example.com/article"
        )
        coEvery { getArticleUseCase(1) } returns DomainResult.Success(article)

        viewModel.fetchArticle(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ArticleDetailsUiState.Success)
        assertEquals(article, (state as ArticleDetailsUiState.Success).data)
        coVerify { getArticleUseCase(1) }
    }

    @Test
    fun `fetchArticle sets error state when use case returns failure`() = runTest {
        coEvery { getArticleUseCase(1) } returns DomainResult.Failure(DomainResult.FailureReason.NotFound)

        viewModel.fetchArticle(1)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is ArticleDetailsUiState.Error)
        assertEquals(R.string.error_loading_article, (state as ArticleDetailsUiState.Error).messageRes)
        coVerify { getArticleUseCase(1) }
    }

    @Test
    fun `navigateToWeb emits navigation event with correct uri`() = runTest {
        val article = Article(
            id = 1,
            title = "Test Article",
            summary = "Test Summary",
            authors = "Test Author",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            imageUrl = "http://example.com/image.jpg",
            url = "http://example.com/article"
        )

        val mockUri = mockk<Uri>(relaxed = true)
        mockkStatic(Uri::class)
        every { Uri.parse(any()) } returns mockUri

        viewModel.uiEvents.test {
            viewModel.navigateToWeb(article)
            val event = awaitItem()
            assertTrue(event is ArticleDetailsUiEvent.NavigateToWeb)
            assertEquals(mockUri, (event as ArticleDetailsUiEvent.NavigateToWeb).uri)
        }
    }
}