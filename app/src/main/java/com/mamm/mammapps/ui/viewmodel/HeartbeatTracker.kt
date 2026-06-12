package com.mamm.mammapps.ui.viewmodel

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.player.SendHeartBeatUseCase
import com.mamm.mammapps.ui.constant.PlayerConstant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class HeartbeatTracker @Inject constructor(
    private val sendHeartbeatUseCase: SendHeartBeatUseCase,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "HeartbeatTracker"
    }

    private var heartbeatJob: Job? = null

    /**
     * Inicia el bucle de heartbeat periódico. Envía una señal inicial y luego
     * repite el proceso cada cierto intervalo definido.
     */
    fun startHeartbeat(scope: CoroutineScope, onSessionExpired: suspend (Throwable) -> Unit) {
        heartbeatJob?.cancel()
        heartbeatJob = scope.launch(Dispatchers.IO) {
            logger.debug(TAG, "startHeartbeat Starting to send heartbeat...")
            sendHeartbeatUseCase().onFailure {
                onSessionExpired(it)
            }
            while (true) {
                delay(PlayerConstant.HEARTBEAT_INTERVAL_MS)
                logger.debug(TAG, "startHeartbeat Sending another heartbeat...")
                sendHeartbeatUseCase().onFailure {
                    onSessionExpired(it)
                }
            }
        }
    }

    /**
     * Cancela el bucle periódico de heartbeat.
     */
    fun stopHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = null
    }
}
