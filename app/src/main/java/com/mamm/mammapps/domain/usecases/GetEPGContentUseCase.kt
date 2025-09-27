package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.interfaces.EPGRepository
import java.time.LocalDate
import javax.inject.Inject

class GetEPGContentUseCase @Inject constructor(
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetEPGContentUseCase"
    }

    suspend operator fun invoke(date: LocalDate): Result<List<EPGChannelContent>> {
        return epgRepository.getEPG(date).fold(
            onSuccess = { response ->
                logger.info(TAG, "GetEPGContentUseCase Received successful response")
                Result.success(response)
            },
            onFailure = { exception ->
                logger.error(TAG, "GetEPGContentUseCase Failed: ${exception.message}, $exception")
                Result.failure(exception)
            }
        )
    }


}