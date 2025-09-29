package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class SendQosUseCase @Inject constructor (
    private val playbackRepository: PlaybackRepository
){

    companion object {
        private const val TAG = "SendQosUseCase"
    }

    suspend operator fun invoke(qosData: QosData): Result<Unit> {
        return playbackRepository.sendQosData(qosData = qosData)
    }
}