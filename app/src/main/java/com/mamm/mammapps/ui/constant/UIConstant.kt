package com.mamm.mammapps.ui.constant

object UIConstant {
    const val HOMEGRIDTOP_IMAGE_WIDTH_FRACTION = 0.66f
    const val MAX_ELEMENTS_PER_ROW = 15
    const val MIN_ELEMENTS_DUPLICATE_ROW = 10
}

object PlayerConstant {
    const val MILLISECONDS_TO_BE_LIVE = 60000L
    const val MILLISECONDS_SHOW_PLAYER_CONTROLS = 8000
    const val MILLISECONDS_TIMEBAR_KEYTIME_INCREMENT = 30000L
    const val MILLISECONDS_SHOW_ZAPPER = 8000L
    const val THUMBNAIL_UPDATE_INTERVAL = 500000L
    const val CHANNEL_NUMBER_ZAPPING_WAITTIME = 3000L
    const val M3U8_EXTENSION = ".m3u8"
    const val QOS_REPORT_INTERVAL_MS = 60000L
    const val BOOKMARK_REPORT_INITIAL_DELAY_MS = 120000L
    const val BOOKMARK_REPORT_INTERVAL_MS = 60000L
    const val HEARTBEAT_INTERVAL_MS = 120000L

    // Reintentos del player ante errores transitorios (backoff exponencial)
    const val PLAYER_MAX_RETRIES = 3
    const val PLAYER_RETRY_BASE_DELAY_SECONDS = 5
}