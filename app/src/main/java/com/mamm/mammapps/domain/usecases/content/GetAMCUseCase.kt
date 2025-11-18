package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.usecases.content.GetWarnerUseCase.Companion
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
        return mammRepository.getAMC().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetAMCUseCase Received successful response")
                Result.success(
                    response.toContentUIRows(
                        subgenres = mammRepository.getSubgenreList().getOrThrow())
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetAMCUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}