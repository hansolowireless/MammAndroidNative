package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {
    companion object {
        const val TAG = "LoginUseCase"
    }

    suspend operator fun invoke(username: String, password: String): Result<Unit> {

        if (Config.shouldUseDynamicUrls) {
            repository.checkLocator(username)
                .onSuccess { locatorResponse ->
                    logger.debug(TAG, "invoke Received locator response: $locatorResponse")
                    Config.updateDynamicUrls(locatorResponse)
                    repository.setShowBrandedContentMenus(false)
                }
                .onFailure {
                    logger.error(TAG, "invoke Locator request failed: ${it.message}")
                    Config.resetDynamicUrls()
                    repository.setShowBrandedContentMenus(true)
                }
        }
        else {
            repository.setShowBrandedContentMenus(true)
        }

        return repository.login(username, password).fold(
            onSuccess = { response ->
                logger.debug(TAG, "invoke Login successful, ${response.data}")

                //Limpiar datos de EPG
                epgRepository.clearCache()

                Result.success(Unit)
            },
            onFailure = { exception ->
                logger.error(TAG, "invoke Login failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }
}