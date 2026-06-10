package com.mamm.mammapps.domain.usecases.content

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.MammRepository
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.navigation.model.AppRoute
import javax.inject.Inject

class GetCategoryContentUseCase @Inject constructor(
    private val repository: MammRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetCategoryContentUseCase"
    }

    suspend operator fun invoke(
        categoryId: Int,
        route: AppRoute? = null
    ): Result<Any> {
        return runCatching {
            repository.getExpandedCategoryContent(categoryId).getOrThrow()
        }.onFailure { remoteException ->
            logger.warn(TAG, "GetCategoryContentUseCase remote fetch failed: ${remoteException.message}. Attempting fallback.")

            if (route == null) {
                logger.error(TAG, "Cannot perform fallback: AppRoute is null.")
                return Result.failure(remoteException)
            }

            return runCatching {
                findAndFilterLocalContent(route, categoryId)
            }.getOrElse {
                logger.error(TAG, "GetCategoryContentUseCase No local content for category")
                Result.failure(remoteException)
            }
        }
    }


    private suspend fun findAndFilterLocalContent(route: AppRoute, categoryId: Int): Result<Any> {
        return findContentByRoute(route).mapCatching { localContent ->
            val filteredContent = getResponseFromContentFilteredByCategory(localContent, categoryId)
            filteredContent ?: throw NoSuchElementException("No local content found for category $categoryId")
        }
    }

    /**
     * Obtiene el contenido completo de una fuente de datos basándose únicamente en la ruta.
     * Entrada: ruta. Salida: contenido.
     */
    private suspend fun findContentByRoute(route: AppRoute): Result<Any> {
        logger.debug(TAG, "Executing findContentByRoute for route: ${route.name}")

        return when (route) {
            AppRoute.MOVIES -> repository.getMovies()
            AppRoute.DOCUMENTARIES -> repository.getDocumentaries()
            AppRoute.KIDS -> repository.getKids()
            AppRoute.SPORTS -> repository.getSports()
            AppRoute.WARNER -> repository.getWarner()
            AppRoute.ACONTRA -> repository.getAcontra()
            AppRoute.ADULTS -> repository.getAdults()
            AppRoute.AMC -> repository.getAMC()
            else -> {
                logger.error(TAG, "No specific fallback logic defined for route: ${route.name}")
                Result.failure(NoSuchElementException("No fallback logic for route: ${route.name}"))
            }
        }
    }

    private fun getResponseFromContentFilteredByCategory(content: Any, categoryId: Int) : Any? {
        when (content) {
            is OtherContent -> {
                val vods = content.vods?.filter { it.subgenreById == categoryId }
                val events = content.events?.filter { it.subgenreById == categoryId }

                logger.debug(TAG, "Found ${vods?.size} VODs for category $categoryId")
                logger.debug(TAG, "Found ${events?.size} EVENTS for category $categoryId")

                if (vods != null || events != null) {
                    return OtherContent(
                        events = events,
                        vods = vods
                    )
                }

                return null
            }

            is BrandedContent -> {
                val vods = content.vods?.filter { it.subgenreById == categoryId }
                val events = content.events?.filter { it.subgenreById == categoryId }

                logger.debug(TAG, "Found ${vods?.size} VODs for category $categoryId")
                logger.debug(TAG, "Found ${events?.size} EVENTS for category $categoryId")

                if (vods != null || events != null) {
                    return BrandedContent(
                        featured = content.featured,
                        channels = content.channels,
                        vods = vods,
                        events = events,
                        series = content.series
                    )
                }

                return null
            }

            else -> { return null}
        }
    }
}