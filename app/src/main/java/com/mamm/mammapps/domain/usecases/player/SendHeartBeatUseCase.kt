package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class SendHeartBeatUseCase @Inject constructor (
    private val playbackRepository: PlaybackRepository
){

    companion object {
        private const val TAG = "SendHeartBeatUseCase"
    }

    suspend operator fun invoke(): Result<Unit> {
        return playbackRepository.sendHeartBeat()
    }
}