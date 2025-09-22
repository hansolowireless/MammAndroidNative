package com.mamm.mammapps.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.GetAdultsUseCase
import com.mamm.mammapps.domain.usecases.GetDocumentariesUseCase
import com.mamm.mammapps.domain.usecases.GetEPGContentUseCase
import com.mamm.mammapps.domain.usecases.GetHomeContentUseCase
import com.mamm.mammapps.domain.usecases.GetKidsUseCase
import com.mamm.mammapps.domain.usecases.GetMoviesUseCase
import com.mamm.mammapps.domain.usecases.GetSportsUseCase
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.AppRoute
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentRowUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContentUseCase: GetHomeContentUseCase,
    private val getEPGContentUseCase: GetEPGContentUseCase,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getDocumentariesUseCase: GetDocumentariesUseCase,
    private val getKidsUseCase: GetKidsUseCase,
    private val getAdultsUseCase: GetAdultsUseCase,
    private val getSportsUseCase: GetSportsUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val findLiveEventOnChannelUseCase: FindLiveEventOnChannelUseCase,
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

    private val _focusedContent = MutableStateFlow<ContentEntityUI?>(null)
    val focusedContent: StateFlow<ContentEntityUI?> = _focusedContent.asStateFlow()

    fun content(routeTag: AppRoute) {
        when (routeTag) {
            AppRoute.HOME -> getHomeContent()
            AppRoute.MOVIES -> getMoviesContent()
            AppRoute.DOCUMENTARIES -> getDocumentariesContent()
            AppRoute.KIDS -> getKidsContent()
            AppRoute.SPORTS -> getSportsContent()
            AppRoute.ADULTS -> getAdultsContent()
            else -> logger.debug(TAG, "Route not implemented")
        }
    }

    private fun getHomeContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getHomeContentUseCase()
                .onSuccess { response ->
                    homeContentUI = response
                    homeContentUIState = UIState.Success(homeContentUI)
                }
                .onFailure { exception ->
                    homeContentUIState =
                        UIState.Error(exception.message ?: "Unknown error occurred")
                }

            getEPGContentUseCase(LocalDate.now())
        }
    }

    private fun getMoviesContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getMoviesUseCase().onSuccess { response ->
                homeContentUI = response
                homeContentUIState = UIState.Success(homeContentUI)
            }
                .onFailure { exception ->
                    homeContentUIState =
                        UIState.Error(exception.message ?: "Unknown error occurred")
                }
        }
    }

    private fun getDocumentariesContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getDocumentariesUseCase().onSuccess { response ->
                homeContentUI = response
                homeContentUIState = UIState.Success(homeContentUI)
            }.onFailure { exception ->
                homeContentUIState =
                    UIState.Error(exception.message ?: "Unknown error occurred")
            }
        }
    }

    private fun getAdultsContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getAdultsUseCase().onSuccess { response ->
                homeContentUI = response
                homeContentUIState = UIState.Success(homeContentUI)
            }.onFailure { exception ->
                homeContentUIState =
                    UIState.Error(exception.message ?: "Unknown error occurred")
            }
        }
    }

    private fun getSportsContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getSportsUseCase().onSuccess { response ->
                homeContentUI = response
                homeContentUIState = UIState.Success(homeContentUI)
            }.onFailure { exception ->
                homeContentUIState =
                    UIState.Error(exception.message ?: "Unknown error occurred")
            }
        }
    }

    private fun getKidsContent() {
        homeContentUIState = UIState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            getKidsUseCase().onSuccess { response ->
                homeContentUI = response
                homeContentUIState = UIState.Success(homeContentUI)}
                .onFailure { exception ->
                    homeContentUIState =
                        UIState.Error(exception.message ?: "Unknown error occurred")
                }
        }
    }


    fun findContent(entityUI: ContentEntityUI, routeTag: AppRoute) {
        findContentEntityUseCase(
            identifier = entityUI.identifier,
            routeTag = routeTag
        ).onSuccess { entity ->
            _clickedContent.update { entity }
        }
    }

    fun clearClickedContent() {
        _clickedContent.update { null }
    }

    fun setFocusedContent(content: ContentEntityUI) {
        when (content.identifier) {
            is ContentIdentifier.Channel -> _focusedContent.update {
                findLiveEventOnChannelUseCase(content.identifier.id)?.toContentEntityUI()
                    ?: content
            }

            else -> _focusedContent.update { content }
        }
    }

}