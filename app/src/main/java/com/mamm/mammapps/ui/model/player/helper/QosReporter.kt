package com.mamm.mammapps.ui.model.player.helper

import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.analytics.PlaybackStatsListener
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.model.player.QosData
import com.mamm.mammapps.domain.usecases.player.SendQosUseCase
import com.mamm.mammapps.domain.usecases.player.SendTickerQosUseCase
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.extension.bitsToMegabits
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QosReporter @Inject constructor(
    private val sendQoSUseCase: SendQosUseCase,
    private val sendTickerQosUseCase: SendTickerQosUseCase,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "QosReporter"
    }

    private var qosJob: Job? = null
    val statsListener: PlaybackStatsListener by lazy {
        PlaybackStatsListener(false) { _, _ -> }
    }

    /**
     * Registra el [PlaybackStatsListener] como un analytics listener en el [ExoPlayer].
     */
    fun registerPlayer(player: ExoPlayer) {
        player.addAnalyticsListener(statsListener)
    }

    /**
     * Remueve el [PlaybackStatsListener] del [ExoPlayer].
     */
    fun unregisterPlayer(player: ExoPlayer) {
        player.removeAnalyticsListener(statsListener)
    }

    /**
     * Inicia el bucle periódico de reportes de QoS cada 60 segundos si el reproductor está activo.
     */
    fun startReporting(
        scope: CoroutineScope,
        getPlayer: () -> ExoPlayer?,
        getContent: () -> ContentToPlayUI
    ) {
        qosJob?.cancel()
        qosJob = scope.launch {
            while (true) {
                delay(PlayerConstant.QOS_REPORT_INTERVAL_MS)
                val player = getPlayer()
                val content = getContent()
                if (player != null) {
                    val qosData = createQosData(player, content)
                    logger.debug(TAG, "startQoSReporting Calling QoS...")
                    withContext(Dispatchers.IO) {
                        sendQoSUseCase(qosData)
                    }
                }
            }
        }
    }

    /**
     * Cancela el bucle periódico de reportes de QoS.
     */
    fun stopReporting() {
        qosJob?.cancel()
        qosJob = null
    }

    /**
     * Lanza una corrutina en segundo plano para enviar el QoS del ticker.
     */
    fun callQoSTicker(scope: CoroutineScope, identifier: ContentIdentifier) {
        scope.launch(Dispatchers.IO) {
            sendTickerQosUseCase(identifier = identifier)
        }
    }

    /**
     * Construye el objeto [QosData] necesario para los reportes de calidad del reproductor.
     */
    private fun createQosData(player: ExoPlayer, content: ContentToPlayUI): QosData {
        return QosData(
            playerBw = statsListener.playbackStats?.meanBandwidth?.bitsToMegabits().toString(),
            activeTrack = player.videoFormat?.height?.toString() ?: "0",
            videoBw = player.videoFormat?.bitrate?.toString() ?: "0",
            bufTime = statsListener.playbackStats?.rebufferRate?.toString() ?: "0",
            loadLatency = statsListener.playbackStats?.meanJoinTimeMs?.toString() ?: "0",
            playTime = "0.0",
            primaryNode = player.currentMediaItem?.localConfiguration?.uri?.host ?: "",
            id = content.identifier.id.toString(),
            type = content.identifier.getQoSString()
        )
    }
}