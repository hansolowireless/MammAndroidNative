package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.model.AboutInfo
import com.mamm.mammapps.domain.model.DownloadSpeedResult
import com.mamm.mammapps.domain.interfaces.DiagnosticsRepository
import com.mamm.mammapps.domain.model.DiagnosticInfo
import javax.inject.Inject

class DiagnosticsRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDataSource,
    private val remoteDatasource: RemoteDatasource,
    private val logger: Logger
) : DiagnosticsRepository
{
    companion object {
        private const val TAG = "DiagnosticsRepositoryImpl"
    }

    override suspend fun getAboutInfo(): Result<AboutInfo> {
        return runCatching {
            AboutInfo(
                appVersion = localDatasource.getApplicationVersion(),
                userName = localDatasource.getUserCredentials().first.orEmpty(),
                userIp = remoteDatasource.getCurrentUserIp()
            )
        }
    }

    override suspend fun getDiagNodes(): Result<DiagnosticInfo> {
        return runCatching {
            remoteDatasource.getDiagNodes().toDomain()
        }.onFailure {
            logger.error(TAG, "getDiagNodes Failed: ${it}")
        }
    }

    /**
     * Implementación de la prueba de velocidad.
     * Simplemente llama al método correspondiente en el [DiagnosticRemoteDataSource].
     */
    override suspend fun performDownloadSpeedTest(url: String): Result<DownloadSpeedResult> {
        return remoteDatasource.performDownloadSpeedTest(url)
    }
}