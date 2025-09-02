package com.mamm.mammapps.domain.usecases

import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.repository.SessionRepository
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.usecases.LoginUseCase.Companion
import javax.inject.Inject

class AutoLoginUseCase @Inject constructor(
    private val repository: MammRepository,
    private val session: SessionRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "AutoLoginUseCase"
    }

    suspend operator fun invoke(): Result<Unit> {
        logger.debug(TAG, "Checking for stored credentials...")

        return repository.getUserCredentials().fold(
            onSuccess = { (username, password) ->
                if (username != null && password != null) {
                    logger.debug(TAG, "autologinUseCase Found valid credentials, attempting auto-login...")

                    if (Config.shouldUseDynamicUrls) {
                        repository.checkLocator(username).onSuccess { locatorResponse ->
                            logger.debug(LoginUseCase.TAG, "invoke Received locator response: $locatorResponse")
                            Config.updateDynamicUrls(locatorResponse)
                        }
                    }

                    repository.login(username, password).fold(
                        onSuccess = { response ->
                            logger.debug(TAG, "autologinUseCase Auto-login successful")
                            response.data?.let { session.assignLoginData(it) }

                            Result.success(Unit)
                        },
                        onFailure = { exception ->
                            logger.debug(TAG, "autologinUseCase Auto-login failed: ${exception.message}")
                            Result.failure(exception)
                        }
                    )
                } else {
                    logger.debug(TAG, "autologinUseCase Stored credentials are incomplete")
                    Result.failure(Exception("autologinUseCase Incomplete credentials"))
                }
            },
            onFailure = { exception ->
                logger.debug(TAG, "autologinUseCase No valid stored credentials: ${exception.message}")
                Result.failure(exception)
            }
        )
    }
}