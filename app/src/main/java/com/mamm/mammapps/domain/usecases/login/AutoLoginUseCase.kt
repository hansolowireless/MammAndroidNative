package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class AutoLoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "AutoLoginUseCase"
    }

    suspend operator fun invoke(): Result<Unit> {
        logger.debug(TAG, "Checking for stored credentials...")

        return repository.autoLogin().fold(
            onSuccess = {
                logger.debug(TAG, "autologinUseCase Auto-login successful")
                Result.success(Unit)
            },
            onFailure = { exception ->
                logger.debug(TAG, "autologinUseCase Auto-login failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }
}