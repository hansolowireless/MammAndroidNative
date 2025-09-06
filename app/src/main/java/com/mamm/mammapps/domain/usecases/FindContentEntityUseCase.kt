package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import javax.inject.Inject

class FindContentEntityUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        const val TAG = "FindContentEntityUseCase"
    }

    operator fun invoke(identifier: ContentIdentifier): Result<Any> {
        return repository.findContent(identifier = identifier)
            ?.onSuccess { result ->
                logger.debug(TAG, "invoke Received content: $result")
            }
            ?.onFailure { exception ->
                logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
            } ?: Result.failure(Exception("Content not found"))
    }
}