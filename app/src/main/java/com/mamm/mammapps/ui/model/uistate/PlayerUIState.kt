package com.mamm.mammapps.ui.model.uistate

import com.mamm.mammapps.ui.model.player.PlayerErrorType

sealed class PlayerUIState : UIState<Nothing>() {
    data object Idle : PlayerUIState()
    data object Playing : PlayerUIState()
    data object Paused : PlayerUIState()

    /**
     * Error de reproducción.
     * @param type tipo de error para mostrar el mensaje localizado adecuado.
     * @param retrySecondsRemaining segundos que faltan para el próximo reintento
     *   (solo en errores transitorios mientras se reintenta); null si no hay reintento en curso.
     */
    data class Error(
        val type: PlayerErrorType,
        val retrySecondsRemaining: Int? = null,
    ) : PlayerUIState()

    data object Session: PlayerUIState()
}






