package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.domain.interfaces.EPGRepository
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import javax.inject.Inject

class FindLiveEventOnChannelUseCase @Inject constructor(
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {

    companion object {
        private const val CHECK_INTERVAL_MS = 5 * 60 * 1000L
        private const val TAG = "FindLiveEventOnChannelUseCase"
    }

    operator fun invoke (channelId: Int) : EPGEvent? {
        return epgRepository.getLiveEventForChannel(channelId)
    }

    fun observeLiveEvents(channelId: Int): Flow<EPGEvent?> = flow {
        var currentEvent: EPGEvent? = null

        runCatching { invoke(channelId) }
            .onSuccess {
                currentEvent = it
                emit(it)
            }
            .onFailure { logger.error(TAG, "Error getting initial event for channel $channelId") }

        while (currentCoroutineContext().isActive) {
            val nextCheckTime = currentEvent?.endDateTime?.let { endTime ->
                val timeUntilEnd = endTime.toInstant().toEpochMilli() - System.currentTimeMillis()
                timeUntilEnd.coerceAtLeast(1000L)
            } ?: (CHECK_INTERVAL_MS)

            delay(nextCheckTime)

            runCatching { invoke(channelId) }
                .onSuccess { newEvent ->
                    if (newEvent?.id != currentEvent?.id) {
                        currentEvent = newEvent
                        emit(newEvent)
                    }
                }
                .onFailure { logger.error(TAG,"Error checking event for channel $channelId") }
        }
    }
        .distinctUntilChanged { old, new -> old?.id == new?.id }
}