package android.meli.feature.articledetails

import android.meli.core.domain.model.Article
import android.meli.core.ui.ErrorWithRetry
import android.meli.core.ui.IsInLandscape
import android.meli.core.ui.Loader
import android.meli.core.ui.theme.MyApplicationTheme
import android.meli.feature.R
import android.meli.feature.routes.Destination
import android.meli.feature.utils.navigateTo
import android.meli.feature.utils.updatedAtFormatted
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.toRoute
import coil3.compose.AsyncImage
import timber.log.Timber

fun NavGraphBuilder.articleDetailsScreen(
    onNavigateBack: () -> Unit = {},
) {
    composable<Destination.ArticleDetails> { backStackEntry ->
        val articleId = backStackEntry.toRoute<Destination.ArticleDetails>().id
        MyApplicationTheme {
            ArticleDetailsScreen(
                articleId = articleId,
                onNavigateBack = onNavigateBack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailsScreen(
    viewModel: ArticleDetailsViewModel = hiltViewModel(),
    articleId: Int,
    onNavigateBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    Scaffold { paddingValues ->
        ArticleDetailsContent(
            paddingValues = paddingValues,
            uiState = uiState,
            articleId = articleId,
            onNavigateToWeb = viewModel::navigateToWeb,
            onFetchArticle = viewModel::fetchArticle,
        )
        TransparentToolbar(onNavigateBack = onNavigateBack)
        UIEventsEffect(viewModel)
    }
    LaunchedEffect(Unit) {
        viewModel.fetchArticle(articleId)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransparentToolbar(
    onNavigateBack: () -> Unit
) {
    TopAppBar(
        title = { },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
        ),
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.back),
                    tint = Color.White
                )
            }
        }
    )
}

@Composable
private fun ArticleDetailsContent(
    paddingValues: PaddingValues = PaddingValues(),
    uiState: ArticleDetailsUiState,
    articleId: Int = 0,
    onNavigateToWeb: (Article) -> Unit = {},
    onFetchArticle: (Int) -> Unit = {},
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when (val state = uiState) {
            is ArticleDetailsUiState.Loading -> Loader()
            is ArticleDetailsUiState.Success -> {
                ArticleDetails(state.data, onNavigateToWeb)
            }
            is ArticleDetailsUiState.Error -> {
                ErrorWithRetry(
                    message = stringResource(state.messageRes),
                    retryText = stringResource(id = R.string.retry),
                    onRetry = { onFetchArticle(articleId) }
                )
            }
        }
    }
}

@Composable
fun ArticleDetails(
    article: Article,
    onWebsiteClick: (Article) -> Unit = {},
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Column {
            article.imageUrl?.let { imageUrl ->
                AsyncImage(
                    model = imageUrl,
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (IsInLandscape()) 200.dp else 300.dp),
                    placeholder = painterResource(R.drawable.ic_image),
                    error = painterResource(R.drawable.ic_image),
                )
            }
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = article.updatedAtFormatted(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = article.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    modifier = Modifier.padding(vertical = 16.dp),
                    text = article.summary,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    text = article.authors,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                article.url?.let { website ->
                    Button(
                        onClick = { onWebsiteClick(article) },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(text = "Read more on Website")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ArticlesListScreenPreview(
    @PreviewParameter(ArticleDetailsUiStateProvider::class) uiState: ArticleDetailsUiState
) {
    MyApplicationTheme {
        ArticleDetailsContent(uiState = uiState)
    }
}

@Composable
private fun UIEventsEffect(
    viewModel: ArticleDetailsViewModel,
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    LaunchedEffect(viewModel.uiEvents, lifecycle) {
        viewModel.uiEvents
            .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
            .collect { event ->
                when (event) {
                    is ArticleDetailsUiEvent.NavigateToWeb -> {
                        Timber.d("Navigating to web: $event")
                        context.navigateTo(event.uri)
                    }
                    // Add more events here if needed
                }
            }
    }
}

/**
 * PreviewParameterProvider for ArticleDetailsUiState
 */
class ArticleDetailsUiStateProvider : PreviewParameterProvider<ArticleDetailsUiState> {
    override val values: Sequence<ArticleDetailsUiState>
        get() = sequenceOf(
            ArticleDetailsUiState.Loading,
            ArticleDetailsUiState.Success(
                Article(
                    id = 1,
                    title = "Single Article Example",
                    summary = "Preview with just one article",
                    imageUrl = "https://example.com/images/single.png",
                    publishedAt = "2021-05-18T13:43:19.863000Z",
                    updatedAt = "2021-05-18T13:43:19.863000Z",
                    url = "https://example.com/articles/single-article",
                    authors = "Single Author",
                )
            ),
            ArticleDetailsUiState.Error(R.string.error_loading_article)
        )
}