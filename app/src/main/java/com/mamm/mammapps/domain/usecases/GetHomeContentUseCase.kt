package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.repository.SessionRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val session: SessionRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetHomeContentUseCase"
    }

    suspend operator fun invoke(): Result<GetHomeContentResponse> {
        return repository.getHomeContent(session.jsonFile ?: "").fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetHomeContentUseCase Received successful response")
                Result.success(response)
            },
            onFailure = { exception ->
                logger.debug(TAG, "GetHomeContentUseCase Failed: ${exception.message}, $exception")
                Result.failure(exception)
            }
        )
    }

}