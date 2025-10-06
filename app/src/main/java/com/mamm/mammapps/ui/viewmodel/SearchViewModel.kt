package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.domain.usecases.SearchContentUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.search.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val searchQuery: String = "",
    val suggestions: List<String> = emptyList(),
    val resultsState: UIState<List<ContentEntityUI>> = UIState.Idle,
    val isFinalSearch: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchContentUseCase: SearchContentUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "SearchViewModel"
    }

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Bookmark>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    init {
        // Este Flow usa searchContentUseCase para obtener sugerencias
        _uiState
            .debounce(300)
            .distinctUntilChanged { old, new -> old.searchQuery == new.searchQuery }
            .filter { !it.isFinalSearch && it.searchQuery.length > 2 }
            .flatMapLatest { state ->
                // Reutilizamos el mismo UseCase
                searchContentUseCase(state.searchQuery)
            }
            .onEach { resource ->
                // Cuando el recurso sea exitoso, extraemos solo los títulos para las sugerencias
                if (resource is Resource.Success) {
                    val titles = resource.data?.mapNotNull { it.title } ?: emptyList()
                    _uiState.update { it.copy(suggestions = titles.distinct()) } // Usamos distinct() para evitar sugerencias duplicadas
                }
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(newQuery: String) {
        // Al escribir, volvemos al estado Idle para los resultados
        _uiState.update {
            it.copy(
                searchQuery = newQuery,
                isFinalSearch = false,
                resultsState = UIState.Idle, // Resetea el estado de los resultados
                suggestions = if (newQuery.isBlank()) emptyList() else it.suggestions
            )
        }
    }

    fun onSearchSubmitted() {
        val query = _uiState.value.searchQuery
        if (query.isBlank()) return

        viewModelScope.launch {
            // Oculta las sugerencias y marca que estamos en una búsqueda final
            _uiState.update { it.copy(suggestions = emptyList(), isFinalSearch = true) }

            // Volvemos a usar el mismo UseCase para la búsqueda final
            searchContentUseCase(query).collect { resource ->
                val newResultsState = when (resource) {
                    is Resource.Loading<*> -> UIState.Loading
                    is Resource.Success -> {
                        // Aquí mapeamos el Bookmark completo a ContentEntityUI
                        resource.data?.let { data ->
                            _searchResults.update { data }
                            val uiResults = data.mapNotNull { it.toContentEntityUI() }
                            UIState.Success(uiResults)
                        } ?: run {
                            logger.error(TAG, "onSearchSubmitted No se encontraron resultados para la búsqueda: $query")
                            UIState.Error("No se encontraron resultados para la búsqueda: $query")
                        }
                    }
                    is Resource.Error -> UIState.Error(resource.message ?: "Error desconocido")
                }
                _uiState.update { it.copy(resultsState = newResultsState) }
            }
        }
    }
}
