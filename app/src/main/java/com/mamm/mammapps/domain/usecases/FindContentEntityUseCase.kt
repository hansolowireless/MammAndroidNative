package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.CustomizedContent
import javax.inject.Inject

class FindContentEntityUseCase @Inject constructor(
    private val repository: MammRepository,
    private val customContentRepository: CustomContentRepository,
    private val logger: Logger
) {

    companion object {
        const val TAG = "FindContentEntityUseCase"
    }

    operator fun invoke(
        identifier: ContentIdentifier,
        customContent: CustomizedContent = CustomizedContent.None,
        routeTag: AppRoute = AppRoute.HOME
    ): Result<Any> {

        when (routeTag) {
            AppRoute.HOME -> {
                if (customContent is CustomizedContent.None) {
                    return repository.findHomeContent(identifier = identifier)
                        ?.onSuccess { result ->
                            logger.debug(TAG, "invoke Received content: $result")
                        }
                        ?.onFailure { exception ->
                            logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                        } ?: Result.failure(Exception("Content not found"))
                } else {
                    return customContentRepository.findContent(
                        contentId = identifier.id,
                        contentType = customContent
                    )?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
                }
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

            AppRoute.WARNER -> {
                return repository.findWarnerContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }

            AppRoute.AMC -> {
                return repository.findAMCContent(identifier = identifier)
                    ?.onSuccess { result ->
                        logger.debug(TAG, "invoke Received content: $result")
                    }
                    ?.onFailure { exception ->
                        logger.debug(TAG, "invoke Failed: ${exception.message}, $exception")
                    } ?: Result.failure(Exception("Content not found"))
            }

            AppRoute.ACONTRA -> {
                return repository.findAcontraContent(identifier = identifier)
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