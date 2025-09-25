package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentIdentifier
import javax.inject.Inject

class FindContentEntityUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        const val TAG = "FindContentEntityUseCase"
    }

    operator fun invoke(identifier: ContentIdentifier, routeTag: AppRoute = AppRoute.HOME): Result<Any> {

        when (routeTag) {
            AppRoute.HOME -> {
                return repository.findHomeContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            AppRoute.MOVIES -> {
                return repository.findMovieContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            AppRoute.DOCUMENTARIES -> {
                return repository.findDocumentaryContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            AppRoute.SPORTS -> {
                return repository.findSportsContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            AppRoute.KIDS -> {
                return repository.findKidsContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            AppRoute.ADULTS -> {
                return repository.findAdultContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }
            else -> {
                logger.debug(TAG, "invoke Route not implemented")
                return Result.failure(Exception("FindContentEntityUseCase Route not implemented"))
            }
        }

    }
}