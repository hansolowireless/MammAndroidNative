package com.mamm.mammapps.domain.usecases.logout

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.interfaces.LoginRepository
import javax.inject.Inject

/**
 * Caso de uso para gestionar el cierre de sesión del usuario.
 * Su única responsabilidad es llamar al repositorio para que limpie
 * todos los datos de la sesión y locales.
 */
class LogoutUseCase @Inject constructor(
    private val repository: LoginRepository,
    private val epgRepository: EPGRepository,
    private val logger: Logger
) {
    operator fun invoke() {
        epgRepository.clearCache()
        repository.logout()
    }
}