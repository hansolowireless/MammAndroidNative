package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.TextInput
import com.mamm.mammapps.ui.component.search.InitialPrompt
import com.mamm.mammapps.ui.component.search.SearchResultsGrid
import com.mamm.mammapps.ui.component.search.SuggestionsList
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.SearchViewModel


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel(),
    onContentClick: (Bookmark) -> Unit
) {
    // Usamos collectAsStateWithLifecycle para una recolección de estado más segura
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextInput(
            value = uiState.searchQuery,
            onValueChange = viewModel::onSearchQueryChange,
            label = stringResource(id = R.string.search_field_label),
            keyboardType = KeyboardType.Text,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    viewModel.onSearchSubmitted()
                }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Lógica de renderizado condicional
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter // Alineamos arriba para que las listas empiecen desde ahí
        ) {
            // Mostramos sugerencias si las hay Y NO es una búsqueda final
            if (uiState.suggestions.isNotEmpty() && !uiState.isFinalSearch) {
                SuggestionsList(
                    suggestions = uiState.suggestions,
                    onSuggestionClick = { suggestion ->
                        keyboardController?.hide()
                        viewModel.onSearchQueryChange(suggestion)
                        viewModel.onSearchSubmitted()
                    }
                )
            } else {
                // Si no hay sugerencias, manejamos los estados de la búsqueda final
                when (val state = uiState.resultsState) {
                    is UIState.Loading -> {
                        LoadingSpinner()
                    }

                    is UIState.Error -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = state.message,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    is UIState.Success -> {
                        if (state.data.isNotEmpty()) {
                            SearchResultsGrid(
                                results = state.data,
                                onContentClick = { contentUI ->
                                    searchResults.find { result -> result.id == contentUI.identifier.id }?.let {
                                        onContentClick(it)
                                    }
                                }
                            )
                        } else if (uiState.isFinalSearch) {
                            // Búsqueda final sin resultados
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text(text = stringResource(id = R.string.search_no_results, uiState.searchQuery))
                            }
                        } else {
                            // Estado por defecto (antes de la primera búsqueda)
                            InitialPrompt()
                        }
                    }

                    is UIState.Idle -> {
                        // Estado inicial o cuando se borra el texto
                        InitialPrompt()
                    }
                }
            }
        }
    }
}
