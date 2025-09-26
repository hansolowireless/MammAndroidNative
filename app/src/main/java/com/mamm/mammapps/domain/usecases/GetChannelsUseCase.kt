package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetChannelsUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentEntityUI>> {
        return repository.getHomeContent().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetHomeContentUseCase Received successful response")
                response.channels?.let { channels ->
                    Result.success(channels.map { it.toContentEntityUI() })
                } ?: run {
                    logger.error(TAG, "GetHomeContentUseCase Failed: channels is null")
                    Result.failure(Exception("Channels list is null"))
                }
            },
            onFailure = { exception ->
                logger.error(TAG, "GetHomeContentUseCase Failed: ${exception.message}, $exception")
                Result.failure(exception)
            }
        )
    }

}