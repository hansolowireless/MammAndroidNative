package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.model.player.Ticker
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class ObserveChannelTickerUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val INITIAL_GRACE_PERIOD_MS = 30_000L
        private const val CHANNEL_TICKER_INTERVAL_MS = 90_000L
        private const val TAG = "ObserveChannelTickerUseCase"
    }

    operator fun invoke(channelId: Int, type: String): Flow<Ticker?> = flow {
        delay(INITIAL_GRACE_PERIOD_MS)
        while (currentCoroutineContext().isActive) {
            playbackRepository.getChannelTicker(channelId, type)
                .onSuccess { ticker ->
                    logger.info(TAG, "invoke canal=$channelId type=$type → ${if (ticker != null) "ticker recibido (id=${ticker.campaignId})" else "sin ticker"}")
                    emit(ticker)
                }
                .onFailure { logger.error(TAG, "Error getting channel ticker: ${it.message}") }
            delay(CHANNEL_TICKER_INTERVAL_MS)
        }
    }
}
