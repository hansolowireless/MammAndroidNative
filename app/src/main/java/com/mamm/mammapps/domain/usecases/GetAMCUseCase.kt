package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetAMCUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object Companion {
        private const val TAG = "GetAMCUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.findGenreWithId(1).fold(
            onSuccess = { genreResult ->
                mammRepository.getAMC().fold(
                    onSuccess = { response ->
                        logger.debug(TAG, "GetAMCUseCase Received successful response")
                        Result.success(response.toContentUIRows(genreResult))
                    },
                    onFailure = { exception ->
                        logger.error(TAG, "GetAMCUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetAMCUseCase Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}