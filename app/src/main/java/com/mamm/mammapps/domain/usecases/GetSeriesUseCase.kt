package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI
import javax.inject.Inject

class GetSeriesUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetSeriesUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        return mammRepository.findGenreWithId(2).fold(
            onSuccess = { genreResult ->
                mammRepository.getHomeContent().fold(
                    onSuccess = { response ->
                        response.series?.let { series ->
                            Result.success(response.series.toContentUIRows(genreResult))
                        } ?: run {
                            logger.debug(TAG, "GetHomeContentUseCase Failed: channels is null")
                            Result.failure(Exception("Channels list is null"))
                        }
                    },
                    onFailure = { exception ->
                        logger.error(TAG, "GetSeriesUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.error(TAG, "GetSeriesUseCase Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}