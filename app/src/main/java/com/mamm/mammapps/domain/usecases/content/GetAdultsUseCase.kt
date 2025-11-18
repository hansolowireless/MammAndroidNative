package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.usecases.content.GetAcontraUseCase.Companion
import com.mamm.mammapps.ui.mapper.insertChannelRow
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetAdultsUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetAdultsUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.getAdults().fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetAcontraUseCase Received successful response")
                Result.success(
                    response
                        .toContentUIRows(subgenres = mammRepository.getSubgenreList().getOrThrow())
                        .insertChannelRow(response.channels)
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetAcontraUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}