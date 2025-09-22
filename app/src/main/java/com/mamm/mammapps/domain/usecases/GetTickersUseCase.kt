package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.GetTickersResponse
import com.mamm.mammapps.data.model.player.Ticker
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTickersUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L
        private const val TAG = "GetTickersUseCase"
    }

    suspend operator fun invoke(): GetTickersResponse {
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

    fun observeTickers(): Flow<List<Ticker>> = flow {
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
                        emit(response.tickers)
                    }
                }
                .onFailure {
                    logger.error(TAG, "Error getting list of Tickers, ${it.message}")
                    // Optionally emit an empty list or handle the error in another way
                    // emit(GetTickersResponse(emptyList(), "")) // Example: emit empty on error
                }
            delay(CHECK_INTERVAL_MS)
        }
    }
}