package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.EPGEvent
import com.mamm.mammapps.domain.interfaces.EPGRepository
import javax.inject.Inject

class FindLiveEventOnChannelUseCase @Inject constructor(
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {

    operator fun invoke (channelId: Int) : EPGEvent? {
        return epgRepository.getLiveEventForChannel(channelId)
    }
}