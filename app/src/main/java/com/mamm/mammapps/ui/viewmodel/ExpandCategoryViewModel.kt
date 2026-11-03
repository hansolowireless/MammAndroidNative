package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.usecases.content.GetCategoryContentUseCase
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.uistate.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ExpandCategoryViewModel @Inject constructor(
    private val getCategoryContentUseCase: GetCategoryContentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<Any>>(UIState.Loading)
    val uiState: StateFlow<UIState<Any>> = _uiState.asStateFlow()

    // Para restaurar el foco al volver de Detail
    var lastClickedIndex: Int? = null
        private set

    private var loadedCategoryId: Int? = null

    fun getContent(
        categoryId: Int?,
        route: AppRoute?
    ) {
        if (categoryId == null) {
            _uiState.value = UIState.Error()
            return
        }

        // Al volver de Detail la pantalla se recompone: no recargar para no perder scroll ni foco
        if (categoryId == loadedCategoryId && _uiState.value is UIState.Success) return
        loadedCategoryId = categoryId
        lastClickedIndex = null

        _uiState.value = UIState.Loading

        viewModelScope.launch (Dispatchers.IO) {
            getCategoryContentUseCase(
                categoryId = categoryId,
                route = route
            )
                .onSuccess { response ->
                _uiState.value = UIState.Success(response)
            }.onFailure {
                _uiState.value = UIState.Error(throwable = it)
            }
        }

    }

    fun setLastClickedIndex(index: Int) {
        lastClickedIndex = index
    }

}