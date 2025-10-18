package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.domain.interfaces.MammRepository
import javax.inject.Inject

class GetCategoryContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetCategoryContentUseCase"
    }

    suspend operator fun invoke(categoryId: Int): Result<GetOtherContentResponse> {
        return repository.getExpandedCategoryContent(categoryId = categoryId).fold(
            onSuccess = { response ->
                Result.success(response)
            },
            onFailure = {
                logger.error(TAG, "GetCategoryContentUseCase Failed: ${it.message}")
                Result.failure(it)
            }
        )
    }
}