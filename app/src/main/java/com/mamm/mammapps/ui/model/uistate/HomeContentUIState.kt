package com.mamm.mammapps.ui.model.uistate

import com.mamm.mammapps.ui.model.ContentRowUI

sealed class HomeContentUIState : UIState<Nothing>() {
    data object Idle : HomeContentUIState()
    data object Loading : HomeContentUIState()
    data object Restricted : HomeContentUIState()
    data object RequestContent: HomeContentUIState()
    data object IncorrectPin : HomeContentUIState()
    data class Success(val data: List<ContentRowUI>) : HomeContentUIState()
    data class Error(val message: String) : HomeContentUIState()
}