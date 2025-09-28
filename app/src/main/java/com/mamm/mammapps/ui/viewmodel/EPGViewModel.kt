package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.GetEPGContentUseCase
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.viewmodel.ChannelsViewModel.Companion.TAG
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
class EPGViewModel @Inject constructor(
    private val getEPGContentUseCase: GetEPGContentUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _epgUIState = MutableStateFlow<UIState<List<EPGChannelContent>>>(UIState.Loading)
    val epgUIState: StateFlow<UIState<List<EPGChannelContent>>> = _epgUIState.asStateFlow()

    private val _playedChannel = MutableStateFlow<Channel?>(null)
    val playedChannel: StateFlow<Channel?> = _playedChannel.asStateFlow()

    fun getEPGContent(date: LocalDate) {
        viewModelScope.launch(Dispatchers.IO) {
            _epgUIState.update { UIState.Loading }

            getEPGContentUseCase(date)
                .onSuccess { epgData ->
                    _epgUIState.update { UIState.Success(epgData) }
                }
                .onFailure { exception ->
                    _epgUIState.update {
                        UIState.Error(exception.message ?: "Unknown error occurred")
                    }
                }
        }
    }

    fun findChannel (event: EPGEvent) {
        val channelIdentifier = ContentIdentifier.Channel(event.getChannelId())

        findContentEntityUseCase(channelIdentifier, AppRoute.HOME).onSuccess { entity ->
            if (entity is Channel) _playedChannel.update { entity } else logger.error(TAG, "findChannel Found entity is not a channel")
        }
    }

    fun clearPlayedChannel() {
        _playedChannel.update { null }
    }

}