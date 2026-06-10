package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.model.serie.SerieInfo
import javax.inject.Inject

class GetSeasonsInfoUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetSeasonsInfoUseCase"
    }

    suspend operator fun invoke(serieId: Int): Result<SerieInfo> {
        return mammRepository.getSeasonsInfo(serieId = serieId).fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetSeasonsInfoUseCase Received successful response")
                Result.success(response)
            },
            onFailure = { exception ->
                logger.error(TAG, "GetSeasonsInfoUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}