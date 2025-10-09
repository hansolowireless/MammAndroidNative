package com.mamm.mammapps.domain.usecases.login

import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val session: SessionManager,
    private val logger: Logger
) {
    companion object {
        const val TAG = "LoginUseCase"
    }

    suspend operator fun invoke(username: String, password: String): Result<Unit> {

        if (Config.shouldUseDynamicUrls) {
            repository.checkLocator(username).onSuccess { locatorResponse ->
                logger.debug(TAG, "invoke Received locator response: $locatorResponse")
                Config.updateDynamicUrls(locatorResponse)
            }
        }

        return repository.login(username, password).fold(
            onSuccess = { response ->
                logger.debug(TAG, "invoke Login successful, ${response.data}")
                response.data?.let { session.assignLoginData(it) }

                // Guardar credenciales tras login exitoso
                repository.saveUserCredentials(username, password)
                logger.debug(TAG, "User credentials saved securely")

                Result.success(Unit)
            },
            onFailure = { exception ->
                logger.error(TAG, "invoke Login failed: ${exception.message}")
                Result.failure(exception)
            }
        )
    }
}