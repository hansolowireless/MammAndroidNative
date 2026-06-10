package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.model.about.AboutInfo
import com.mamm.mammapps.domain.model.about.DownloadSpeedResult
import com.mamm.mammapps.domain.interfaces.DiagnosticsRepository
import com.mamm.mammapps.domain.model.about.DiagnosticInfo
import javax.inject.Inject

class DiagnosticsRepositoryImpl @Inject constructor(
    private val localDatasource: LocalDataSource,
    private val remoteDatasource: RemoteDatasource,
    private val sessionDatasource: SessionDatasource,
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
                userName = sessionDatasource.getUserCredentials().first.orEmpty(),
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