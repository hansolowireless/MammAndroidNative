package com.mamm.mammapps.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.GetHomeContentUseCase
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.mapper.toContentRows
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContentUseCase: GetHomeContentUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        const val TAG = "HomeViewModel"
    }

    var homeContentUIState by mutableStateOf<UIState<List<ContentRowUI>>>(UIState.Idle)
        private set

    var homeContentUI by mutableStateOf<List<ContentRowUI>>(emptyList())
        private set

    private val _clickedContent = MutableStateFlow<Any?>(null)
    val clickedContent: StateFlow<Any?> = _clickedContent.asStateFlow()

    fun getHomeContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch {
            getHomeContentUseCase()
                .onSuccess { response ->
                    homeContentUI = response.toContentRows()
                    homeContentUIState = UIState.Success(homeContentUI)
                }
                .onFailure { exception ->
                    homeContentUIState = UIState.Error(exception.message ?: "Unknown error occurred")
                }
        }
    }

    fun findContent(entityUI: ContentEntityUI) {
        findContentEntityUseCase(entityUI.identifier).onSuccess { entity ->
            _clickedContent.update { entity }
        }
    }

    fun clearClickedContent() {
        _clickedContent.value = null
    }

}