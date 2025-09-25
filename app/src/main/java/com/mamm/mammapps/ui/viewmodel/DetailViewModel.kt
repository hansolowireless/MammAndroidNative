package com.mamm.mammapps.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mamm.mammapps.domain.usecases.GetSeasonsInfoUseCase
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.SeasonUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val getSeasonsInfoUseCase: GetSeasonsInfoUseCase
) : ViewModel() {

    private val _seasonInfoUIState = MutableStateFlow<UIState<List<SeasonUI>>>(UIState.Idle)
    val seasonInfoUIState = _seasonInfoUIState.asStateFlow()

    fun getSeasonInfo(content: ContentEntityUI) {
        if (content.identifier is ContentIdentifier.Serie) {
            viewModelScope.launch(Dispatchers.IO) {
                getSeasonsInfoUseCase(content.identifier.id).onSuccess { seasonList ->
                    _seasonInfoUIState.value = UIState.Success(seasonList)
                }.onFailure {
                    _seasonInfoUIState.value = UIState.Error(it.message.orEmpty())
                }

            }

        }

    }

}
