package com.mamm.mammapps.ui.viewmodel

import android.provider.Contacts.Intents.UI
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.usecases.GetEPGContentUseCase
import com.mamm.mammapps.ui.common.UIState
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
    private val getEPGContentUseCase: GetEPGContentUseCase
) : ViewModel() {

    private val _epgUIState = MutableStateFlow<UIState<List<EPGChannelContent>>>(UIState.Loading)
    val epgUIState: StateFlow<UIState<List<EPGChannelContent>>> = _epgUIState.asStateFlow()

    fun getEPGContent() {
        viewModelScope.launch(Dispatchers.IO) {
            _epgUIState.update { UIState.Loading }

            getEPGContentUseCase(LocalDate.now())
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
}