package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetMemoriesUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetMemoriesUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return repository.getMemories().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetMemoriesUseCase Received successful response")
                Result.success(response.toContentUIRows())
            },
            onFailure = { exception ->
                logger.error(TAG, "GetMemoriesUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }
}