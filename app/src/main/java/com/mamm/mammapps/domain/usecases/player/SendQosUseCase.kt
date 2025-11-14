package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import javax.inject.Inject

class SendQosUseCase @Inject constructor (
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "c"
    }

    suspend operator fun invoke(qosData: QosData): Result<Unit> {
        return playbackRepository.sendQosData(qosData = qosData)
    }
}