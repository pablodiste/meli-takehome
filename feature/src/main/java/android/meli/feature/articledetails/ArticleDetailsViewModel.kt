package android.meli.feature.articledetails

import android.meli.core.domain.DomainResult
import android.meli.core.domain.GetArticleUseCase
import android.meli.core.domain.model.Article
import android.meli.feature.R
import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArticleDetailsViewModel @Inject constructor(
    private val getArticleUseCase: GetArticleUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<ArticleDetailsUiState> = MutableStateFlow(ArticleDetailsUiState.Loading)
    val uiState: StateFlow<ArticleDetailsUiState> = _uiState
    private val _uiEvents: MutableSharedFlow<ArticleDetailsUiEvent> = MutableSharedFlow()
    val uiEvents: SharedFlow<ArticleDetailsUiEvent> = _uiEvents

    fun fetchArticle(articleId: Int) {
        viewModelScope.launch {
            val result: DomainResult<Article> = getArticleUseCase(articleId)
            when (result) {
                is DomainResult.Success -> _uiState.value = ArticleDetailsUiState.Success(result.data)
                is DomainResult.Failure -> _uiState.value = ArticleDetailsUiState.Error(R.string.error_loading_article)
            }
        }
    }

    fun navigateToWeb(article: Article) {
        val link = article.url
        viewModelScope.launch {
            _uiEvents.emit(ArticleDetailsUiEvent.NavigateToWeb(link.toUri()))
        }
    }

}

sealed interface ArticleDetailsUiState {
    object Loading : ArticleDetailsUiState
    data class Error(val messageRes: Int) : ArticleDetailsUiState
    data class Success(val data: Article) : ArticleDetailsUiState
}

sealed interface ArticleDetailsUiEvent {
    data class NavigateToWeb(val uri: Uri) : ArticleDetailsUiEvent
}