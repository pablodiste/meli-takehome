package android.meli.feature.articlelist

import android.meli.core.domain.SearchArticlesUseCase
import android.meli.core.domain.model.Article
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class ArticleListViewModel @Inject constructor(
    private val searchArticlesUseCase: SearchArticlesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val uiState: StateFlow<PagingData<Article>> = _searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            searchArticlesUseCase(query)
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Eagerly, PagingData.empty())

    private val _uiEvents: MutableSharedFlow<ArticleListUiEvent> = MutableSharedFlow()
    val uiEvents: SharedFlow<ArticleListUiEvent> = _uiEvents

    fun updateSearchQuery(text: String) {
        _searchQuery.value = text
    }

    fun search(text: String) {
        _searchQuery.value = text
    }

    fun articleSelected(article: Article) {
        viewModelScope.launch {
            _uiEvents.emit(ArticleListUiEvent.NavigateToDetails(article.id))
        }
    }
}

sealed interface ArticleListUiEvent {
    data class NavigateToDetails(val id: Int) : ArticleListUiEvent
    object ShowFetchError: ArticleListUiEvent
}