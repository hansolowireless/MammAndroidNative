package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject
import androidx.core.net.toUri
import com.mamm.mammapps.ui.mapper.toContentUIRows
import com.mamm.mammapps.ui.model.ContentRowUI

class GetMoviesUseCase @Inject constructor(
    private val mammRepository: MammRepository,
    private val sessionManager: SessionManager,
    private val logger: Logger
){

    companion object {
        private const val TAG = "GetMoviesUseCase"
    }

    suspend operator fun invoke(): Result<List<ContentRowUI>> {
        val pathParam = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            ?: return Result.failure(IllegalStateException("No valid path segment found in session file"))

        return mammRepository.findGenreWithId(1).fold(
            onSuccess = { genreResult ->
                mammRepository.getMovies(pathParam).fold(
                    onSuccess = { response ->
                        logger.debug(TAG, "GetMoviesUseCase Received successful response")
                        Result.success(response.toContentUIRows(genreResult))
                    },
                    onFailure = { exception ->
                        logger.debug(TAG, "GetMoviesUseCase Failed: ${exception.message}")
                        Result.failure(exception)
                    }
                )
            },
            onFailure = { exception ->
                logger.debug(TAG, "Genre lookup failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }

}