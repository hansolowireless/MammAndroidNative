package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class GetOperatorLogoUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(): Result<String> {
        return repository.getOperatorLogoUrl()
            .onSuccess {
                logger.debug("GetOperatorLogoUseCase", "Operator logo URL: $it")
            }
            .onFailure {
                logger.error("GetOperatorLogoUseCase", "Error getting operator logo URL: $it")
            }
    }
}