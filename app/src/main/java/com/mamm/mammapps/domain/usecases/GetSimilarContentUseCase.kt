package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import javax.inject.Inject

class GetSimilarContentUseCase @Inject constructor(
    private val customContentRepository: CustomContentRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetSimilarContentUseCase"
    }

    suspend operator fun invoke(subgenreId: Int): Result<List<Bookmark>> {
        return customContentRepository.getSimilar(subgenreId = subgenreId)
            .map { it.vods.orEmpty() + it.cutvs.orEmpty() }
            .onFailure { logger.error(TAG, "invoke failed: ${it.message}, $it") }
    }

}