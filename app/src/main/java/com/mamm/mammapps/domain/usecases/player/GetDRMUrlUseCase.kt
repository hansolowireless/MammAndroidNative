package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.model.DrmInfo
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import javax.inject.Inject

class GetDRMUrlUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository
) {

    companion object {
        private const val TAG = "GetDRMUrlUseCase"
    }
    suspend operator fun invoke(
        content: ContentToPlayUI
    ): Result<DrmInfo> {
        return runCatching {
            val (url, customData) = playbackRepository.getDRMUrl(
                content = content
            ).getOrThrow()
            DrmInfo(
                drmUrl = url,
                customData = customData
            )
        }
    }
}