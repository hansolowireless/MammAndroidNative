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

        return repository.getCredentials().fold(
            onSuccess = { (username, password) ->
                if (username != null && password != null) {
                    logger.debug(TAG, "autologinUseCase Found valid credentials, attempting auto-login...")
                    if (Config.shouldUseDynamicUrls) {
                        repository.checkLocator(username)
                            .onSuccess { locatorResponse ->
                                logger.debug(TAG, "invoke Received locator response: $locatorResponse")
                                Config.updateDynamicUrls(locatorResponse)
                                repository.setShowBrandedContentMenus(false)
                            }
                            .onFailure {
                                logger.error(TAG, "invoke Locator failed: $it")
                                Config.resetDynamicUrls()
                                repository.setShowBrandedContentMenus(true)
                            }
                    }
                    else {
                        repository.setShowBrandedContentMenus(true)
                    }

                    repository.login(username, password).fold(
                        onSuccess = { response ->
                            logger.debug(TAG, "autologinUseCase Auto-login successful")
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