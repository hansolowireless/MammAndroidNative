package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.data.model.serie.TbSeason
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.GetSeasonsInfoUseCase
import com.mamm.mammapps.domain.usecases.GetSimilarContentUseCase
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.extension.catchupIsAvailable
import com.mamm.mammapps.ui.mapper.toSeasonUIList
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.SeasonUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSeasonsInfoUseCase: GetSeasonsInfoUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val getSimilarContentUseCase: GetSimilarContentUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "DetailViewModel"
    }

    private var routeTag: AppRoute = AppRoute.HOME

    private val _showPlayButton = MutableStateFlow<Boolean>(false)
    val showPlayButton: StateFlow<Boolean> = _showPlayButton.asStateFlow()

    private val _clickedContent = MutableStateFlow<Any?>(null)
    val clickedContent: StateFlow<Any?> = _clickedContent.asStateFlow()

    private val _seasonInfoUIState = MutableStateFlow<UIState<List<SeasonUI>>>(UIState.Idle)
    val seasonInfoUIState = _seasonInfoUIState.asStateFlow()

    private var seasonListOriginal: List<TbSeason> = mutableListOf()

    private val _similarContent = MutableStateFlow<List<Recommended>?>(null)
    val similarContent: StateFlow<List<Recommended>?> = _similarContent.asStateFlow()

    fun setRouteTag(routeTag: AppRoute) {
        this.routeTag = routeTag
    }

    fun setShowPlayButton(content: ContentEntityUI) {
        //Caso de venir en la EPG
        if (content.identifier is ContentIdentifier.Event && routeTag == AppRoute.EPG) {
            content.detailInfo?.channelId?.let {
                findContentEntityUseCase(identifier = ContentIdentifier.Channel(id = it))
                    .onSuccess { channel ->
                        val catchupHours = (channel as Channel).catchupHours ?: 0
                        _showPlayButton.update { content.catchupIsAvailable(catchupHours) }
                        return
                    }.onFailure {
                        logger.error(
                            TAG,
                            "setShowPlayButton channel for content was not found ${it.message}"
                        )
                        _showPlayButton.update { false }
                        return
                    }
            }
        }

        //Si no venimos de la EPG
        _showPlayButton.update { content.identifier !is ContentIdentifier.Serie && !content.isFuture() }
    }

    fun getSeasonInfo(content: ContentEntityUI) {
        if (content.identifier is ContentIdentifier.Serie) {
            viewModelScope.launch(Dispatchers.IO) {
                getSeasonsInfoUseCase(content.identifier.id).onSuccess { seasonInfoResponse ->
                    _seasonInfoUIState.value = UIState.Success(seasonInfoResponse.toSeasonUIList())
                    seasonInfoResponse.tbSeasons?.let { seasonListOriginal = it }
                }.onFailure {
                    _seasonInfoUIState.value = UIState.Error(it.message.orEmpty())
                }
            }
        }
    }

    fun findContent(entityUI: ContentEntityUI) {
        findContentEntityUseCase(
            identifier = entityUI.identifier,
            customContent = entityUI.customContentType,
            routeTag = routeTag
        ).onSuccess { entity ->
            _clickedContent.update { entity }
        }
    }

    fun findEpisode(seasonOrder: Int, episodeId: Int) {
        seasonListOriginal.find { it.getOrder() == seasonOrder }
            ?.let { seasonTb ->
                seasonTb.tbContentSeasons?.find { it.contentDetails?.getId() == episodeId }
                    ?.let { episodeTb ->
                        _clickedContent.update { episodeTb }
                    }
            }
    }

    fun getSimilar(subgenreId: Int?) {
        subgenreId?.let {
            viewModelScope.launch(Dispatchers.IO) {
                getSimilarContentUseCase(subgenreId)
                    .onSuccess { result ->
                        _similarContent.update { result }
                    }
            }
        } ?: run {
            logger.debug(TAG, "getSimilar: subgenreId is null")
        }
    }

    fun clearClickedContent() {
        _clickedContent.update { null }
    }

}
