package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.about.AboutInfo
import com.mamm.mammapps.domain.model.about.DownloadSpeedResult
import com.mamm.mammapps.domain.model.about.DiagnosticInfo

interface DiagnosticsRepository {

    suspend fun getAboutInfo() : Result<AboutInfo>
    suspend fun getDiagNodes() : Result<DiagnosticInfo>
    /**
     * Inicia una prueba de velocidad de descarga usando la URL proporcionada.
     * Delega la ejecución al RemoteDataSource.
     * @param url La URL del archivo a descargar para la prueba.
     * @return Un [Result] que contiene [DownloadSpeedResult] si es exitoso, o una excepción si falla.
     */
    suspend fun performDownloadSpeedTest(url: String): Result<DownloadSpeedResult>
}