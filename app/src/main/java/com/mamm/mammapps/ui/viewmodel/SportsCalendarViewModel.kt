package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.SportsEvent
import com.mamm.mammapps.domain.usecases.content.GetSportsCalendarUseCase
import com.mamm.mammapps.domain.usecases.content.FindChannelForMatchUseCase
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.DetailInfoUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.model.uistate.UIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject



@HiltViewModel
class SportsCalendarViewModel @Inject constructor(
    private val getSportsCalendarUseCase: GetSportsCalendarUseCase,
    private val findChannelForMatchUseCase: FindChannelForMatchUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UIState<Unit>>(UIState.Idle)
    val uiState: StateFlow<UIState<Unit>> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<Channel>()
    val navigationEvent: SharedFlow<Channel> = _navigationEvent.asSharedFlow()

    private val _competitions = MutableStateFlow<List<String>>(emptyList())
    val competitions: StateFlow<List<String>> = _competitions.asStateFlow()

    private val _selectedCompetition = MutableStateFlow<String?>(null)
    val selectedCompetition: StateFlow<String?> = _selectedCompetition.asStateFlow()

    private val _eventsGroupedByDay = MutableStateFlow<Map<String, List<ContentListUI>>>(emptyMap())
    val eventsGroupedByDay: StateFlow<Map<String, List<ContentListUI>>> = _eventsGroupedByDay.asStateFlow()

    private var allEvents: List<SportsEvent> = emptyList()

    private val dayFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("es", "ES"))
    private val timeFormat = DateTimeFormatter.ofPattern("HH:mm", Locale("es", "ES"))

    fun loadEvents() {
        if (_uiState.value !is UIState.Idle) return
        
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { UIState.Loading }
            
            getSportsCalendarUseCase().onSuccess { events ->
                allEvents = events
                
                // Extract competition names from channelId (e.g. LALIGA_EA_SPORTS.es -> LALIGA EA SPORTS)
                val competitions = events.map { it.channelId }
                    .map { formatCompetitionName(it) }
                    .distinct()
                    .sorted()
                
                updateFilteredEvents(competitions, null)
                
            }.onFailure { exception ->
                _uiState.update { 
                    UIState.Error(
                        message = exception.message.orEmpty(),
                        throwable = exception
                    ) 
                }
            }
        }
    }

    fun selectTab(competition: String?) {
        if (_uiState.value is UIState.Success) {
            updateFilteredEvents(_competitions.value, competition)
        }
    }

    private fun updateFilteredEvents(newCompetitions: List<String>, newSelectedCompetition: String?) {
        val filtered = if (newSelectedCompetition == null) {
            allEvents
        } else {
            allEvents.filter { formatCompetitionName(it.channelId) == newSelectedCompetition }
        }

        val grouped = groupAndMapEvents(filtered)

        _competitions.value = newCompetitions
        _selectedCompetition.value = newSelectedCompetition
        _eventsGroupedByDay.value = grouped
        
        _uiState.update { UIState.Success(Unit) }
    }

    private fun groupAndMapEvents(events: List<SportsEvent>): Map<String, List<ContentListUI>> {
        val groupedModel = events.groupBy { event ->
            event.startTime?.let { dayFormat.format(it).replaceFirstChar { char -> char.uppercase() } } ?: "Fecha desconocida"
        }

        // Map to ContentListUI
        return groupedModel.mapValues { entry ->
            entry.value.mapIndexed { index, event ->
                val timeString = event.startTime?.let { timeFormat.format(it) } ?: ""
                val desc = if (timeString.isNotEmpty()) "$timeString - ${event.description}" else event.description
                
                ContentListUI(
                    identifier = ContentIdentifier.Event(index), // Using index for dummy ID as we don't have a specific ID
                    imageUrl = event.horizontalImage,
                    title = event.title,
                    detailInfo = DetailInfoUI(description = desc),
                    liveEventInfo = LiveEventInfoUI(
                        eventStart = event.startTime,
                        eventEnd = event.endTime,
                        title = event.title
                    )
                )
            }
        }
    }

    private fun formatCompetitionName(channelId: String): String {
        return channelId.replace(".es", "")
            .replace("_", " ")
            .uppercase()
    }

    fun onEventClicked(event: ContentListUI) {
        viewModelScope.launch {
            val originalEvent = allEvents.find { it.title == event.title }
            findChannelForMatchUseCase(event.title, originalEvent?.channelId).onSuccess { channel ->
                _navigationEvent.emit(channel)
            }.onFailure {
                // Silent fallback or standard handling
            }
        }
    }
}
