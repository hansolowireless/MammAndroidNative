package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.repository.SessionRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetHomeContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val session: SessionRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetHomeContentUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return repository.getHomeContent(session.jsonFile ?: "").fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetHomeContentUseCase Received successful response")
                Result.success(response.toContentRows())
            },
            onFailure = { exception ->
                logger.debug(TAG, "GetHomeContentUseCase Failed: ${exception.message}, $exception")
                Result.failure(exception)
            }
        )
    }

}