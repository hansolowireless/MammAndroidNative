package com.mamm.mammapps.ui.viewmodel

import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.content.GetAMCUseCase
import com.mamm.mammapps.domain.usecases.content.GetAcontraUseCase
import com.mamm.mammapps.domain.usecases.content.GetAdultsUseCase
import com.mamm.mammapps.domain.usecases.content.GetDocumentariesUseCase
import com.mamm.mammapps.domain.usecases.content.GetEPGContentUseCase
import com.mamm.mammapps.domain.usecases.content.GetHomeContentUseCase
import com.mamm.mammapps.domain.usecases.content.GetKidsUseCase
import com.mamm.mammapps.domain.usecases.content.GetMoviesUseCase
import com.mamm.mammapps.domain.usecases.content.GetSeriesUseCase
import com.mamm.mammapps.domain.usecases.content.GetSportsUseCase
import com.mamm.mammapps.domain.usecases.content.GetWarnerUseCase
import com.mamm.mammapps.domain.usecases.login.GetOperatorLogoUseCase
import com.mamm.mammapps.domain.usecases.pin.ShouldRequestPinUseCase
import com.mamm.mammapps.domain.usecases.pin.ValidatePinUseCase
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentRowUI
import com.mamm.mammapps.ui.model.uistate.HomeContentUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import androidx.compose.runtime.State

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getOperatorLogoUrlUseCase: GetOperatorLogoUseCase,
    private val getHomeContentUseCase: GetHomeContentUseCase,
    private val getEPGContentUseCase: GetEPGContentUseCase,
    private val getSeriesUseCase: GetSeriesUseCase,
    private val getMoviesUseCase: GetMoviesUseCase,
    private val getDocumentariesUseCase: GetDocumentariesUseCase,
    private val getKidsUseCase: GetKidsUseCase,
    private val getAdultsUseCase: GetAdultsUseCase,
    private val getSportsUseCase: GetSportsUseCase,
    private val getWarnerUseCase: GetWarnerUseCase,
    private val getAMCUseCase: GetAMCUseCase,
    private val getAcontraUseCase: GetAcontraUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val findLiveEventOnChannelUseCase: FindLiveEventOnChannelUseCase,
    private val shouldRequestPinUseCase: ShouldRequestPinUseCase,
    private val validatePinUseCase: ValidatePinUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        const val TAG = "HomeViewModel"
    }

    var homeContentUIState by mutableStateOf<HomeContentUIState>(HomeContentUIState.Idle)
        private set

    private val _operatorLogo = MutableStateFlow<String?>(null)
    val operatorLogo : StateFlow<String?> = _operatorLogo.asStateFlow()

    private val _homeContentUI = MutableStateFlow<List<ContentRowUI>>(emptyList())
    val homeContentUI: StateFlow<List<ContentRowUI>> = _homeContentUI.asStateFlow()

    private val _mobileFeatured = MutableStateFlow<List<ContentEntityUI>?>(null)
    val mobileFeatured: StateFlow<List<ContentEntityUI>?> = _mobileFeatured.asStateFlow()

    private val _clickedContent = MutableStateFlow<Any?>(null)
    val clickedContent: StateFlow<Any?> = _clickedContent.asStateFlow()

    private val _focusedContent = MutableStateFlow<ContentEntityUI?>(null)
    val focusedContent: StateFlow<ContentEntityUI?> = _focusedContent.asStateFlow()

    private val _lastClickedItemIndex = MutableStateFlow<Int?>(0)
    val lastClickedItemIndex: StateFlow<Int?> = _lastClickedItemIndex.asStateFlow()

    private val _rememberedRowState = mutableStateOf<Pair<Int, Int>?>(null)
    val rememberedRowState: State<Pair<Int, Int>?> = _rememberedRowState

    fun rememberRowState(state: LazyListState) {
        _rememberedRowState.value = Pair(state.firstVisibleItemIndex, state.firstVisibleItemScrollOffset)
        logger.debug(TAG, "rememberRowState saved: Index=${state.firstVisibleItemIndex}, Offset=${state.firstVisibleItemScrollOffset}")
    }

    private fun setLoadingStateWithLogo() {
        viewModelScope.launch(Dispatchers.IO) {
            getOperatorLogoUrlUseCase().onSuccess { logo ->
                _operatorLogo.update { logo }
                homeContentUIState = HomeContentUIState.Loading(logo)
            }.onFailure {
                logger.error(TAG, "setLoadingStateWithLogo Error getting operator logo URL: $it")
                homeContentUIState = HomeContentUIState.Loading(null)
            }
        }
    }

    fun checkRestrictedScreen(routeTag: AppRoute) {
        homeContentUIState = when (routeTag) {
            AppRoute.ADULTS -> {
                if (shouldRequestPinUseCase()) HomeContentUIState.PinRestriction else HomeContentUIState.RequestContent
            }

            else -> HomeContentUIState.RequestContent
        }
    }

    fun validatePin(pin: String) {
        homeContentUIState = if (validatePinUseCase(pin)) {
            HomeContentUIState.RequestContent
        } else {
            HomeContentUIState.IncorrectPin
        }
    }

    fun content(routeTag: AppRoute) {
        when (routeTag) {
            AppRoute.HOME -> loadContent {
                getHomeContentUseCase().onSuccess {
                    viewModelScope.launch(Dispatchers.IO) {
                        getEPGContentUseCase(LocalDate.now())
                    }
                }
            }

            AppRoute.SERIES -> loadContent { getSeriesUseCase() }
            AppRoute.MOVIES -> loadContent { getMoviesUseCase() }
            AppRoute.DOCUMENTARIES -> loadContent { getDocumentariesUseCase() }
            AppRoute.KIDS -> loadContent { getKidsUseCase() }
            AppRoute.SPORTS -> loadContent { getSportsUseCase() }
            AppRoute.ADULTS -> loadContent { getAdultsUseCase() }
            AppRoute.WARNER -> loadContent { getWarnerUseCase() }
            AppRoute.AMC -> loadContent { getAMCUseCase() }
            AppRoute.ACONTRA -> loadContent { getAcontraUseCase() }
            else -> logger.debug(TAG, "Route not implemented")
        }
    }

    private fun loadContent(useCase: suspend () -> Result<List<ContentRowUI>>) {
        viewModelScope.launch(Dispatchers.IO) {
            setLoadingStateWithLogo()
            useCase()
                .onSuccess { response ->
                    _homeContentUI.update { response }
                    homeContentUIState = HomeContentUIState.Success(_homeContentUI.value)
                }
                .onFailure { exception ->
                    homeContentUIState = HomeContentUIState.Error(
                        exception.message.orEmpty(),
                        throwable = exception
                    )
                }
        }
    }

    fun setFirstFocusedContent() {
        if (_homeContentUI.value.isNotEmpty() && _homeContentUI.value.first().items.isNotEmpty())
            _focusedContent.update { _homeContentUI.value.first().items.first() }
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

        if (content.isFeatured) {
            _focusedContent.update { content }
            return
        }

        when (content.identifier) {
            is ContentIdentifier.Channel -> _focusedContent.update {
                findLiveEventOnChannelUseCase(content.identifier.id)?.toContentEntityUI()
                    ?: content
            }

            else -> _focusedContent.update { content }
        }
    }

    fun setLastClickedIndex(index: Int) {
        _lastClickedItemIndex.update { index }
    }

    fun setLastClickedIndexToNull() {
        _lastClickedItemIndex.update { null }
    }

    fun setLastClickedIndexToZero() {
        _lastClickedItemIndex.update { 0 }
    }

}