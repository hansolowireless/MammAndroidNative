package com.mamm.mammapps.ui.viewmodel

import com.google.android.exoplayer2.ExoPlayer
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.player.SendBookmarkUseCase
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.mamm.mammapps.ui.constant.PlayerConstant
import javax.inject.Inject

class BookmarkTracker @Inject constructor(
    private val sendBookmarkUseCase: SendBookmarkUseCase,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "BookmarkTracker"
    }

    private var bookmarkJob: Job? = null

    /**
     * Inicia el reporte periódico del bookmark si el contenido es VoD o Event.
     * Retrasa el primer envío 2 minutos, y después envía periódicamente cada 60 segundos.
     */
    fun startTracking(
        scope: CoroutineScope,
        getPlayer: () -> ExoPlayer?,
        getContent: () -> ContentToPlayUI
    ) {
        val content = getContent()
        if (content.identifier is ContentIdentifier.VoD || content.identifier is ContentIdentifier.Event) {
            bookmarkJob?.cancel()
            bookmarkJob = scope.launch {
                logger.debug(TAG, "startBookmarkReporting in ${PlayerConstant.BOOKMARK_REPORT_INITIAL_DELAY_MS} millisecs...")
                delay(PlayerConstant.BOOKMARK_REPORT_INITIAL_DELAY_MS) // 2 minutos inicial
                while (true) {
                    val player = getPlayer()
                    val currentContent = getContent()
                    if (player != null) {
                        val currentPosition = player.currentPosition
                        logger.debug(TAG, "sendBookmarkStamps position = $currentPosition")
                        runCatching {
                            sendBookmarkUseCase(
                                content = currentContent,
                                time = currentPosition
                            )
                        }.onFailure {
                            logger.error(TAG, "sendBookmarkStamps - Error sending bookmark: ${it.message}")
                        }
                    }
                    delay(PlayerConstant.BOOKMARK_REPORT_INTERVAL_MS)
                }
            }
        } else {
            logger.info(
                TAG,
                "startBookmarkReporting Content is not VOD or Event, won't start bookmark report"
            )
        }
    }

    /**
     * Cancela el bucle periódico de reportes de bookmark, liberando cualquier referencia.
     */
    fun stopTracking() {
        bookmarkJob?.cancel()
        bookmarkJob = null
    }
}
