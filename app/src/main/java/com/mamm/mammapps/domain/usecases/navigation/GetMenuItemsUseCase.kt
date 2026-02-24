package com.mamm.mammapps.domain.usecases.navigation

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.LoginRepository
import com.mamm.mammapps.navigation.MenuItems
import com.mamm.mammapps.navigation.model.AppRoute
import javax.inject.Inject

class GetMenuItemsUseCase @Inject constructor(
    private val loginRepository: LoginRepository,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "GetMenuItemsUseCase"
    }

    operator fun invoke(): Result<List<AppRoute>> {
        return runCatching {
            // Obtenemos los valores. Si alguno es Failure, getOrThrow() salta al bloque onFailure
            val isHoreca = loginRepository.getUserIsHoreca().getOrThrow()
            val showBrandedContent = loginRepository.getShowBrandedContentMenus().getOrThrow()

            // Determinamos la lista inicial
            val list = if (showBrandedContent) {
                logger.debug(TAG, "Mostrando contenido branded")
                MenuItems.list
            } else {
                logger.debug(TAG, "Ocultando contenido branded")
                MenuItems.listNoSpanishUserContent
            }

            // Aplicamos el filtro: si NO es Horeca, quitamos SPORTSCALENDAR
            if (!isHoreca) {
                logger.debug(TAG, "Usuario no es Horeca, quitando SPORTSCALENDAR")
                list.filter { it != AppRoute.SPORTSCALENDAR }
            } else {
                list
            }
        }.onFailure {
            logger.error(TAG, "Error al obtener la configuración de menús: ${it.message}")
        }
    }
}