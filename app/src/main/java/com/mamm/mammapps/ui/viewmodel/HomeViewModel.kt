package com.mamm.mammapps.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.usecases.GetHomeContentUseCase
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.model.ContentRowUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContentUseCase: GetHomeContentUseCase
) : ViewModel() {

    var homeContentState by mutableStateOf<UIState<List<ContentRowUI>>>(UIState.Idle)
        private set

    var homeContent by mutableStateOf<List<ContentRowUI>>(emptyList())
        private set

    fun getHomeContent() {
        homeContentState = UIState.Loading
        viewModelScope.launch {
            getHomeContentUseCase()
                .onSuccess { response ->
                    homeContentState = UIState.Success(response)
                    homeContent = response
                }
                .onFailure { exception ->
                    homeContentState = UIState.Error(exception.message ?: "Unknown error occurred")
                }
        }
    }
}