package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetWarnerUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetWarnerUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.getWarner().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetWarnerUseCase Received successful response")
                Result.success(
                    response.toContentUIRows(
                        subgenres = mammRepository.getSubgenreList().getOrThrow())
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetWarnerUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}