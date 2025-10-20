package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.content.GetChannelsUseCase
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

    private var channels: List<Channel> = emptyList()

    private val _channelGenres = MutableStateFlow<Set<String>>(emptySet())
    val channelGenres: StateFlow<Set<String>> = _channelGenres.asStateFlow()

    private val _selectedGenres = MutableStateFlow<Set<String>>(emptySet())
    val selectedGenres: StateFlow<Set<String>> = _selectedGenres.asStateFlow()

    private val _filteredChannels = MutableStateFlow<List<ContentEntityUI>>(emptyList())
    val filteredChannels: StateFlow<List<ContentEntityUI>> = _filteredChannels.asStateFlow()

    fun getChannels() {
        viewModelScope.launch(Dispatchers.IO) {
            getChannelsUseCase().onSuccess { response ->
                channels = response
                _filteredChannels.update { response.map { it.toContentEntityUI() } }
                _channelGenres.update { response.mapNotNull { it.channelGenre }.toSet() }
            }
        }
    }

    fun observeLiveEvents() {
        _focusedContent.value?.let {
            getLiveEventInfoUseCase.observeLiveEvents((it.identifier).getIdValue())
                .onEach { event ->
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

    fun setFirstFocusedContent() {
        _focusedContent.update { _filteredChannels.value.firstOrNull() }
    }

    fun findChannel(content: ContentEntityUI) {
        findContentEntityUseCase(content.identifier, routeTag = AppRoute.HOME).onSuccess { entity ->
            if (entity is Channel) _clickedContent.update { entity } else logger.error(
                TAG,
                "findChannel Found entity is not a channel"
            )
        }
    }

    fun clearClickedContent() {
        _clickedContent.update { null }
    }

    fun filterChannelsByGenres(genres: Set<String>) {
        _selectedGenres.update { genres }
        if (genres.isEmpty()) {
            _filteredChannels.update { channels.map { it.toContentEntityUI() } }
        } else {
            val filteredChannelsDebug = channels.filter { it.channelGenre in genres }
            logger.debug(TAG, "filterChannelsByGenres Filtered channels: $filteredChannels")
            _filteredChannels.update {
                channels.filter { it.channelGenre in genres }.map { it.toContentEntityUI() }
            }
        }
    }

    fun filterChannelsByQuery(query: String) {
        if (query.isNotBlank()) {
            _filteredChannels.update {
                _filteredChannels.value.filter { it.title.contains(query, ignoreCase = true) }
            }
        } else {
            logger.debug(TAG, "filterChannelsByQuery No query, showing all channels")
            filterChannelsByGenres(_selectedGenres.value)
        }
    }

    fun resetSelectedGenres() {
        _selectedGenres.update { emptySet() }
        filterChannelsByGenres(emptySet())
    }

}