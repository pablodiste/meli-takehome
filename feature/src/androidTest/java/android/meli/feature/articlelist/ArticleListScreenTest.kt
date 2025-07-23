package android.meli.feature.articlelist

import android.meli.core.domain.model.Article
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ArticleListScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testArticles = listOf(
        Article(
            id = 1,
            title = "Test Article 1",
            summary = "Summary 1",
            authors = "Author 1",
            updatedAt = "2024-01-01T10:00:00Z",
            publishedAt = "2024-01-01T10:00:00Z",
            imageUrl = "http://example.com/image1.jpg",
            url = "http://example.com/article1"
        ),
        Article(
            id = 2,
            title = "Test Article 2",
            summary = "Summary 2",
            authors = "Author 2",
            updatedAt = "2024-01-02T10:00:00Z",
            publishedAt = "2024-01-02T10:00:00Z",
            imageUrl = "http://example.com/image2.jpg",
            url = "http://example.com/article2"
        )
    )

    @Test
    fun articleList_displaysArticles() {
        val pagingDataFlow = MutableStateFlow(PagingData.from(testArticles))

        composeTestRule.setContent {
            ArticleList(articles = pagingDataFlow.collectAsLazyPagingItems())
        }

        // Verify articles are displayed
        composeTestRule.onNodeWithText("Test Article 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test Article 2").assertIsDisplayed()
        composeTestRule.onNodeWithText("Author 1").assertIsDisplayed()
        composeTestRule.onNodeWithText("Author 2").assertIsDisplayed()
    }

    @Test
    fun articleList_showsEmptyStateWhenNoArticles() {
        val emptyPagingData = PagingData.from<Article>(
            emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.NotLoading(endOfPaginationReached = true),
                append = LoadState.NotLoading(endOfPaginationReached = true),
                prepend = LoadState.NotLoading(endOfPaginationReached = true)
            )
        )
        val pagingDataFlow = MutableStateFlow(emptyPagingData)

        composeTestRule.setContent {
            ArticleList(articles = pagingDataFlow.collectAsLazyPagingItems())
        }

        composeTestRule.onNodeWithText("No articles found").assertIsDisplayed()
    }

    @Test
    fun articleList_showsLoadingState() {
        val loadingPagingData = PagingData.from<Article>(
            emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Loading,
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false)
            )
        )
        val pagingDataFlow = MutableStateFlow(loadingPagingData)

        composeTestRule.setContent {
            ArticleList(articles = pagingDataFlow.collectAsLazyPagingItems())
        }

        composeTestRule.onNodeWithText("Loading articles...").assertIsDisplayed()
    }

    @Test
    fun articleList_showsErrorState() {
        val errorPagingData = PagingData.from<Article>(
            emptyList(),
            sourceLoadStates = LoadStates(
                refresh = LoadState.Error(Exception("Network error")),
                append = LoadState.NotLoading(endOfPaginationReached = false),
                prepend = LoadState.NotLoading(endOfPaginationReached = false)
            )
        )
        val pagingDataFlow = MutableStateFlow(errorPagingData)

        composeTestRule.setContent {
            ArticleList(articles = pagingDataFlow.collectAsLazyPagingItems())
        }

        composeTestRule.onNodeWithText("Error loading news").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun articleItem_triggersClickCallback() {
        var clicked: Boolean = false
        val article = testArticles[0]

        composeTestRule.setContent {
            ArticleItem(
                article = article,
                onPressed = { clicked = true }
            )
        }

        composeTestRule.onNodeWithTag("article_item").performClick()

        assertTrue(clicked)
    }

    @Test
    fun searchBar_displaysCorrectly() {
        var queryChanged = ""
        var searchPressed = ""

        composeTestRule.setContent {
            ArticleSearchBar(
                query = "test query",
                onQueryChange = { queryChanged = it },
                onSearchPressed = { searchPressed = it }
            )
        }

        // Verify search bar elements are displayed
        composeTestRule.onNodeWithTag("search_input").assertIsDisplayed()
    }

    @Test
    fun searchBar_showsClearButtonWhenQueryNotEmpty() {
        var currentQuery = "test"

        composeTestRule.setContent {
            ArticleSearchBar(
                query = currentQuery,
                onQueryChange = { currentQuery = it }
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertIsDisplayed()
    }

    @Test
    fun searchBar_hidesClearButtonWhenQueryEmpty() {
        composeTestRule.setContent {
            ArticleSearchBar(
                query = "",
                onQueryChange = { }
            )
        }

        composeTestRule.onNodeWithContentDescription("Clear search").assertDoesNotExist()
    }

    @Test
    fun homeAppbar_displaysTitle() {
        composeTestRule.setContent {
            HomeAppbar()
        }

        // Note: You might need to replace with the actual string resource value
        composeTestRule.onNodeWithText("Space News").assertIsDisplayed()
    }
}