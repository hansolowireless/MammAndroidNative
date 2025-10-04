package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.GetChannelsUseCase
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val findContentEntityUseCase: FindContentEntityUseCase,
    private val getLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    private val logger: Logger
) : ViewModel() {

    companion object {
        const val TAG = "ChannelsViewModel"
    }

    private val _focusedContent = MutableStateFlow<ContentEntityUI?>(null)
    val focusedContent: StateFlow<ContentEntityUI?> = _focusedContent.asStateFlow()

    private val _liveEventInfo = MutableStateFlow<ContentEntityUI?>(null)
    val liveEventInfo = _liveEventInfo.asStateFlow()

    private val _clickedContent = MutableStateFlow<Channel?>(null)
    val clickedContent: StateFlow<Channel?> = _clickedContent.asStateFlow()

    private val _channels = MutableStateFlow<List<ContentEntityUI>>(emptyList())
    val channels: StateFlow<List<ContentEntityUI>> = _channels.asStateFlow()

    fun getChannels() {
       viewModelScope.launch (Dispatchers.IO) {
           getChannelsUseCase().onSuccess { response ->
               _channels.update { response }
           }
       }
    }

    fun observeLiveEvents() {
        _focusedContent.value?.let {
            getLiveEventInfoUseCase.observeLiveEvents((it.identifier).getIdValue())
                .onEach { event ->
                    // Nuevo evento iniciado o terminado
                    logger.debug(
                        TAG,
                        "startObservingLiveEvents Event changed: ${event?.getTitle()}"
                    )
                    _liveEventInfo.value = event?.toContentEntityUI() ?: it
                }
                .launchIn(viewModelScope)
        }
    }

    fun setFocusedContent(content: ContentEntityUI) {
        _focusedContent.update { content }
    }

    fun findChannel (content: ContentEntityUI) {
        findContentEntityUseCase(content.identifier, routeTag = AppRoute.HOME).onSuccess { entity ->
            if (entity is Channel) _clickedContent.update { entity } else logger.error(TAG, "findChannel Found entity is not a channel")
        }
    }

    fun clearClickedContent() {
        _clickedContent.update { null }
    }

}