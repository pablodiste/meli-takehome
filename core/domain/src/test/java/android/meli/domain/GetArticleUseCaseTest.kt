package android.meli.domain

import android.meli.core.domain.DomainResult
import android.meli.core.domain.GetArticleUseCase
import android.meli.core.domain.model.Article
import android.meli.core.domain.repositories.ArticlesRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetArticleUseCaseTest {

    private lateinit var repository: ArticlesRepository
    private lateinit var getArticleUseCase: GetArticleUseCase

    @Before
    fun setUp() {
        repository = mockk<ArticlesRepository>()
        getArticleUseCase = GetArticleUseCase(repository)
    }

    @Test
    fun `returns article for valid id`() = runTest {
        val expectedArticle = Article(
            id = 1,
            title = "Test Article",
            summary = "A summary for testing",
            authors = "Test Author",
            updatedAt = "2024-01-01",
            publishedAt = "2024-01-01",
            imageUrl = "http://example.com/test.jpg",
            url = "http://example.com/article"
        )
        coEvery { repository.getArticle(1) } returns DomainResult.Success(expectedArticle)

        val result = getArticleUseCase(1)
        assert(result is DomainResult.Success)
        assertEquals(expectedArticle, (result as DomainResult.Success).data)
    }

    @Test
    fun `returns error when repository fails`() = runTest {
        val expectedError = DomainResult.Failure(reason = DomainResult.FailureReason.NotFound)
        coEvery { repository.getArticle(1) } returns expectedError

        val result = getArticleUseCase(1)
        assert(result is DomainResult.Failure)
        assertEquals(DomainResult.FailureReason.NotFound, (result as DomainResult.Failure).reason)
    }
}