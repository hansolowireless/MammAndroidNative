package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

class SendTickerQosUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "SendTickerQosUseCase"
    }

    suspend operator fun invoke() {
        playbackRepository.getTickerQoSData()
            .onSuccess {
                logger.debug(TAG, "ticker qos data $it")
                playbackRepository.sendQosData(it)
            }.onFailure {
                logger.error(TAG, "error getting ticker qos data $it.message")
            }
    }
}