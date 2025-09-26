package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetDocumentariesUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetDocumentariesUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.findGenreWithId(9).fold(
            onSuccess = { genreResult ->
                mammRepository.getDocumentaries().fold(
                    onSuccess = { response ->
                        logger.debug(TAG, "GetDocumentariesUseCase Received successful response")
                        Result.success(response.toContentUIRows(genreResult))
                    },
                    onFailure = { exception ->
                        logger.error(TAG, "GetDocumentariesUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetDocumentariesUseCase Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}