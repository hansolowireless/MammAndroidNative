package com.mamm.mammapps.data.model.player

import com.google.android.exoplayer2.ExoPlayer
import java.util.Date

data class VideoPlayerUIState(
    // Player
//    val player: ExoPlayer? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0,
    val duration: Long = 0,

    // Configuración de contenido (reemplaza VideoPlayerActivityConfig)
//    val videoURL: String? = null,
//    val licenseURL: String? = null,
    val isDevModeOn: Boolean = false,

    // Estados de UI
    val showControls: Boolean = false,
    val showPreview: Boolean = false,
    val hidePreview: Boolean = false,
    val isZapping: Boolean = false,

    // Diálogos
    val showPINDialog: Boolean = false,
    val showTrackSelectionDialog: Boolean = false,
    val showSubtitlesDialog: Boolean = false,
    val showAudioTrackDialog: Boolean = false,
    val isPINValid: Boolean = false,

    // Zapping de canales
    val channelZapNumber: String = "",
    val showChannelZapDisplay: Boolean = false,

    // TSTV (Timeshift TV)
    val tstvMode: Boolean = false,
    val tstvPoint: Date? = null,
    val tstvHourBeginText: String = "",
    val showLiveIndicator: Boolean = false,

    // Preview/Thumbnails
    val thumbnailUrl: String = "",
    val thumbnailPosition: Long = 0,
    val isPreviewEnabled: Boolean = true,

    // Tickers
    val tickers: List<Ticker> = emptyList(),

    // Estados de error
    val error: String? = null
) {
    // Métodos helper que reemplazan los de VideoPlayerActivityConfig
    fun showTickers(): Boolean = tickers.isNotEmpty()
}






