package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetKidsUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetKidsUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.findGenreWithId(15).fold(
            onSuccess = { genreResult ->
                mammRepository.getKids().fold(
                    onSuccess = { response ->
                        logger.debug(TAG, "GetKidsUseCase Received successful response")
                        Result.success(response.toContentUIRows(genreResult))
                    },
                    onFailure = { exception ->
                        logger.debug(TAG, "GetKidsUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.debug(TAG, "GetKidsUseCase Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}