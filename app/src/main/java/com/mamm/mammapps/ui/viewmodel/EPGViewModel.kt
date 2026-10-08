package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.usecases.FindContentEntityUseCase
import com.mamm.mammapps.domain.usecases.content.GetEPGContentUseCase
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.ChannelsViewModel.Companion.TAG
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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

    private val _selectedChannel = MutableStateFlow<Channel?>(null)
    val selectedChannel: StateFlow<Channel?> = _selectedChannel.asStateFlow()

    private val _playedChannel = MutableStateFlow<Channel?>(null)
    val playedChannel: StateFlow<Channel?> = _playedChannel.asStateFlow()

    private val _selectedDate = MutableStateFlow<LocalDate>(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    // Día de la parrilla que hay ahora en epgUIState (null si no hay ninguna cargada)
    private var loadedDate: LocalDate? = null
    private var loadJob: Job? = null

    /**
     * Carga la parrilla de [date]. Si ya se está mostrando ese día (p. ej. al volver del
     * player o del detalle) se refresca por debajo sin pasar por Loading: la parrilla
     * sigue en pantalla, sin spinner y sin perder el scroll.
     */
    fun getEPGContent(date: LocalDate) {
        val isRefresh = loadedDate == date && _epgUIState.value is UIState.Success
        loadJob?.cancel()
        loadJob = viewModelScope.launch(Dispatchers.IO) {
            if (!isRefresh) {
                loadedDate = null
                _epgUIState.update { UIState.Loading }
            }

            getEPGContentUseCase(date)
                .onSuccess { epgData ->
                    val filteredData = epgData.filter { it.channel.isPornChannel == false }
                    loadedDate = date
                    _epgUIState.update { UIState.Success(filteredData) }
                }
                .onFailure { exception ->
                    // El repositorio usa runCatching: una carga cancelada (cambio de día) llega aquí
                    if (exception is CancellationException) return@onFailure
                    // Si falla un refresco se deja la parrilla que ya había
                    if (isRefresh) return@onFailure
                    _epgUIState.update {
                        UIState.Error(exception.message ?: "Unknown error occurred")
                    }
                }
        }
    }

    fun findChannel (event: Event) {
        val channelIdentifier = ContentIdentifier.Channel(event.getChannelId())

        findContentEntityUseCase(
            channelIdentifier,
            routeTag = AppRoute.HOME
        ).onSuccess { entity ->
            if (entity is Channel) _playedChannel.update { entity } else logger.error(TAG, "findChannel Found entity is not a channel")
        }
    }

    fun setSelectedChannel (channel: Channel) {
        _selectedChannel.update{ channel }
    }

    fun setSelectedDate (date: LocalDate) {
        _selectedDate.update { date }
    }

    fun clearPlayedChannel() {
        _playedChannel.update { null }
    }

}