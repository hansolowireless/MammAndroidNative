package com.mamm.mammapps.domain.usecases.diagnostic

import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.model.DownloadSpeedResult
import com.mamm.mammapps.domain.interfaces.DiagnosticsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Orquesta el diagnóstico completo:
 * 1. Obtiene las URLs de los nodos desde DiagnosticsRepository.
 * 2. Ejecuta un test de velocidad para cada una de las 4 URLs.
 * 3. Devuelve una lista de 4 resultados, usando 'null' para los tests que fallen.
 */
class RunFullServerDiagnosticUseCase @Inject constructor(
    private val diagRepository: DiagnosticsRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "RunFullDiagnosticUseCase"
    }

    suspend operator fun invoke(): Result<List<DownloadSpeedResult?>> = withContext(Dispatchers.IO) {
        logger.debug(TAG, "Iniciando diagnóstico integral...")

        // 1. Obtener la configuración de nodos
        diagRepository.getDiagNodes().fold(
            onSuccess = { response ->
                // Extraemos las 4 URLs en un orden fijo
                val urls = listOf(
                    response.node1Url,
                    response.node2Url,
                    response.node3Url,
                    response.node4Url
                )

                // 2. Mapeamos cada URL a un resultado de test (o null si falla)
                val results = urls.map { url ->
                    diagRepository.performDownloadSpeedTest(url).fold(
                        onSuccess = { speedResult ->
                            logger.debug(TAG, "Éxito en [$url]: ${speedResult.speedMbps} Mbps")
                            speedResult // El resultado exitoso
                        },
                        onFailure = { error ->
                            logger.error(TAG, "Error en [$url]: ${error.message}")
                            null // El test falló, devolvemos null
                        }
                    )
                }

                logger.debug(TAG, "Diagnóstico completado. Resultados: ${results.count { it != null }} de 4 exitosos.")
                Result.success(results) // Siempre devolvemos la lista de 4 elementos
            },
            onFailure = { exception ->
                logger.error(TAG, "Fallo crítico al obtener nodos: ${exception.message}")
                Result.failure(exception) // Si no podemos obtener los nodos, el caso de uso falla por completo
            }
        )
    }
}
