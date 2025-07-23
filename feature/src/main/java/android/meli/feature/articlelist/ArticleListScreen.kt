package android.meli.feature.articlelist

import android.meli.core.domain.model.Article
import android.meli.core.ui.EmptyState
import android.meli.core.ui.ErrorWithRetry
import android.meli.core.ui.Loader
import android.meli.core.ui.theme.MyApplicationTheme
import android.meli.feature.R
import android.meli.feature.routes.Destination.ArticleList
import android.meli.feature.utils.updatedAtFormatted
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.flowWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

fun NavGraphBuilder.articleListScreen(
    onNavigateToDetails: (Int) -> Unit = {},
) {
    composable<ArticleList> {
        MyApplicationTheme {
            ArticleListScreen(
                onNavigateToDetails = onNavigateToDetails,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    viewModel: ArticleListViewModel = hiltViewModel(),
    onNavigateToDetails: (Int) -> Unit,
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val articleItems = viewModel.uiState.collectAsLazyPagingItems()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { HomeAppbar() },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->

        // Search bar
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ArticleSearchBar(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onSearchPressed = viewModel::search,
            )
            ArticleList(
                articles = articleItems,
                onPressed = viewModel::articleSelected,
            )
        }

        UIEventsEffect(viewModel,
            onNavigateToDetails = onNavigateToDetails,
            onShowFetchError = { message ->
                coroutineScope.launch {
                    snackbarHostState.showSnackbar(message)
                }
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeAppbar() {
    TopAppBar(
        title = { Text(stringResource(R.string.home_title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
        ),
        actions = {
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ArticleSearchBar(
    modifier: Modifier = Modifier,
    query: String = "",
    onQueryChange: (String) -> Unit = {},
    onSearchPressed: (String) -> Unit = {},
) {
    SearchBar(
        inputField = {
            SearchBarDefaults.InputField(
                modifier = Modifier.testTag("search_input"),
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearchPressed,
                expanded = false,
                onExpandedChange = { },
                placeholder = { Text(stringResource(R.string.search_articles)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                }
            )
        },
        expanded = false,
        onExpandedChange = { },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        windowInsets = WindowInsets(0.dp),
    ) {
        // Empty content since we don't need expanded search suggestions
    }
}

@Composable
fun ArticleList(
    articles: LazyPagingItems<Article>,
    modifier: Modifier = Modifier,
    onPressed: (Article) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (articles.loadState.refresh == LoadState.Loading) {
            item {
                Text(
                    text = stringResource(R.string.loading_articles),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally),
                )
            }
        }

        items(count = articles.itemCount) { index ->
            articles[index]?.let { article ->
                ArticleItem(article = article, onPressed = { onPressed(article) })
            }
        }

        if (articles.loadState.append == LoadState.Loading) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Loader()
                }
            }
        }

        if (articles.loadState.refresh is LoadState.Error || articles.loadState.append is LoadState.Error) {
            item {
                Box(modifier = Modifier.fillMaxWidth()) {
                    ErrorWithRetry(
                        message = stringResource(R.string.error_loading_articles),
                        retryText = stringResource(id = R.string.retry),
                        onRetry = { articles.refresh() }
                    )
                }
            }
        }

        // Show empty state when no items and not loading
        if (articles.itemCount == 0 && articles.loadState.refresh is LoadState.NotLoading) {
            item {
                Box(modifier = Modifier.fillMaxSize()) {
                    EmptyState(text = stringResource(R.string.no_articles_found))
                }
            }
        }
    }
}

@Composable
fun ArticleItem(
    article: Article,
    modifier: Modifier = Modifier,
    onPressed: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPressed.invoke() }
            .testTag("article_item")
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(116.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = article.imageUrl,
            contentDescription = article.title,
            modifier = Modifier
                .padding(8.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.ic_image),
            error = painterResource(R.drawable.ic_image),
        )
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = article.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                maxLines = 2
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.authors,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = article.updatedAtFormatted(),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun UIEventsEffect(
    viewModel: ArticleListViewModel,
    onNavigateToDetails: (Int) -> Unit,
    onShowFetchError: (String) -> Unit,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    LaunchedEffect(viewModel.uiEvents, lifecycle) {
        viewModel.uiEvents
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is ArticleListUiEvent.NavigateToDetails -> {
                        onNavigateToDetails.invoke(event.id)
                    }
                    ArticleListUiEvent.ShowFetchError -> {
                        onShowFetchError(context.getString(R.string.error_fetching_articles))
                    }
                }
            }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlesListScreenPreview(
    @PreviewParameter(ArticleFlowPreviewParameterProvider ::class) articleFlow: Flow<PagingData<Article>>
) {
    MyApplicationTheme {
        ArticleList(articles = articleFlow.collectAsLazyPagingItems())
    }
}

class ArticleFlowPreviewParameterProvider : PreviewParameterProvider<Flow<PagingData<Article>>> {
    override val values: Sequence<Flow<PagingData<Article>>> = sequenceOf(
        // Multiple articles scenario
        MutableStateFlow(PagingData.from(listOf(
            Article(
                id = 1,
                title = "Exploring Kotlin Coroutines",
                summary = "A deep dive into asynchronous programming with Kotlin coroutines.",
                imageUrl = "https://example.com/images/kotlin_coroutines.png",
                publishedAt = "2021-05-18T13:43:19.863000Z",
                updatedAt = "2021-05-18T13:43:19.863000Z",
                url = "https://example.com/articles/kotlin-coroutines",
                authors = "John Doe, Jane Smith",
            ),
            Article(
                id = 2,
                title = "Jetpack Compose: Modern UI Toolkit",
                summary = "Learn how Jetpack Compose simplifies UI development on Android.",
                imageUrl = "https://example.com/images/jetpack_compose.png",
                publishedAt = "2021-05-18T13:43:19.863000Z",
                updatedAt = "2021-05-18T13:43:19.863000Z",
                url = "https://example.com/articles/jetpack-compose",
                authors = "Alice Johnson, Bob Brown",
            )
        ))),
        // Empty list scenario
        MutableStateFlow(PagingData.from(emptyList(),sourceLoadStates =
            LoadStates(
                refresh = LoadState.NotLoading(true),
                append = LoadState.NotLoading(true),
                prepend = LoadState.NotLoading(true),
            ),
        )),
        // Single article scenario
        MutableStateFlow(PagingData.from(listOf(
            Article(
                id = 3,
                title = "Single Article Example",
                summary = "Preview with just one article",
                imageUrl = "https://example.com/images/single.png",
                publishedAt = "2021-05-18T13:43:19.863000Z",
                updatedAt = "2021-05-18T13:43:19.863000Z",
                url = "https://example.com/articles/single-article",
                authors = "Single Author",
            )
        )))
    )
}
