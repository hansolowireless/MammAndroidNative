package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.RouteTag
import javax.inject.Inject

class FindHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        const val TAG = "FindContentEntityUseCase"
    }

    operator fun invoke(identifier: ContentIdentifier, routeTag: RouteTag = RouteTag.HOME): Result<Any> {

        when (routeTag) {
            RouteTag.HOME -> {
                return repository.findHomeContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            RouteTag.MOVIES -> {
                return repository.findMovieContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            else -> {
                logger.debug(TAG, "invoke Route not implemented")
                return Result.failure(Exception("Route not implemented"))
            }
        }

    }
}