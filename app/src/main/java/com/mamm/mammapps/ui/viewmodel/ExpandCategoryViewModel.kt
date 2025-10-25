package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.domain.usecases.content.GetCategoryContentUseCase
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

    private val _uiState = MutableStateFlow<UIState<GetBrandedContentResponse>>(UIState.Loading)
    val uiState: StateFlow<UIState<GetBrandedContentResponse>> = _uiState.asStateFlow()

    fun getContent (categoryId: Int?) {
        if (categoryId == null) {
            _uiState.value = UIState.Error()
            return
        }

        _uiState.value = UIState.Loading

        viewModelScope.launch (Dispatchers.IO) {
            getCategoryContentUseCase(categoryId = categoryId)
                .onSuccess { response ->
                _uiState.value = UIState.Success(response)
            }.onFailure {
                _uiState.value = UIState.Error(throwable = it)
            }
        }

    }

}