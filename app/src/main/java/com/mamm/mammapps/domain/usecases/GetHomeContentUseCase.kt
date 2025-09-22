package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetHomeContentUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return repository.getHomeContent().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetHomeContentUseCase Received successful response")
                Result.success(response.toContentUIRows())
            },
            onFailure = { exception ->
                logger.debug(TAG, "GetHomeContentUseCase Failed: ${exception.message}, $exception")
                Result.failure(exception)
            }
        )
    }

}