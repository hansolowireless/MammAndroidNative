package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetWarnerUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetWarnerUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.findGenreWithId(1).fold(
            onSuccess = { genreResult ->
                mammRepository.getWarner().fold(
                    onSuccess = { response ->
                        logger.debug(TAG, "GetWarnerUseCase Received successful response")
                        Result.success(response.toContentUIRows(genreResult))
                    },
                    onFailure = { exception ->
                        logger.error(TAG, "GetWarnerUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetWarnerUseCase Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}