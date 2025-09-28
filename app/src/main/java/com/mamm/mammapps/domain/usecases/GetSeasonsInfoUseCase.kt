package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.serie.TbSeason
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toSeasonUIList
import com.mamm.mammapps.ui.model.SeasonUI
import javax.inject.Inject

class GetSeasonsInfoUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetSeasonsInfoUseCase"
    }

    suspend operator fun invoke(serieId: Int): Result<Pair<List<SeasonUI>, List<TbSeason>>> {
        return mammRepository.getSeasonsInfo(serieId = serieId).fold(
            onSuccess = { response ->
                logger.debug(TAG, "GetSeasonsInfoUseCase Received successful response")
                Result.success(Pair(response.toSeasonUIList(), response.tbSeasons.orEmpty()))
            },
            onFailure = { exception ->
                logger.error(TAG, "GetSeasonsInfoUseCase Failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}