package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.model.player.TickerInfo
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class GetTickersUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L
        private const val TAG = "GetTickersUseCase"
    }

    suspend operator fun invoke(): TickerInfo {
        return playbackRepository.getTickers().fold(
            onSuccess = {
                it
            },
            onFailure = {
                logger.error(TAG, "Error getting list of Tickers, ${it.message}")
                throw it
            }
        )
    }

    fun observeTickers(): Flow<TickerInfo> = flow {
        var currentFechaGeneracion: String? = null
        while (currentCoroutineContext().isActive) {
            runCatching { invoke() }
                .onSuccess { response ->
                    if (response.fechaGeneracion != currentFechaGeneracion) {
                        logger.info(
                            TAG,
                            "observeTickers Success getting list of tickers, it is a new list"
                        )
                        currentFechaGeneracion = response.fechaGeneracion
                        emit(response)
                    }
                }
                .onFailure {
                    logger.error(TAG, "Error getting list of Tickers, ${it.message}")
                }
            delay(CHECK_INTERVAL_MS)
        }
    }
}