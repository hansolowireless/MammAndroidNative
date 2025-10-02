package com.mamm.mammapps.ui.model.player

import com.mamm.mammapps.ui.common.UIState

sealed class PlayerUIState : UIState<Nothing>() {
    data object Idle : PlayerUIState()
    data object Playing : PlayerUIState()
    data object Paused : PlayerUIState()
    data class Error(val message: String) : PlayerUIState()
}






