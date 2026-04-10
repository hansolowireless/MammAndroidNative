package com.mamm.mammapps.data.model.player.customdatasourcefactory

import com.google.android.exoplayer2.drm.ExoMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.mamm.mammapps.data.logger.Logger
import java.util.UUID

class DynamicHttpMediaDrmCallback(
    defaultLicenseUrl: String,
    forceDefaultLicenseUrl: Boolean = true,
    dataSourceFactory: HttpDataSource.Factory,
    private val logger: Logger,
    private val tokenProvider: () -> String?
) : MediaDrmCallback {

    companion object {
        private const val TAG = "DynamicHttpMediaDrmCallback"
    }

    private val internalCallback = HttpMediaDrmCallback(
        defaultLicenseUrl,
        forceDefaultLicenseUrl,
        dataSourceFactory
    )

    override fun executeProvisionRequest(
        uuid: UUID,
        request: ExoMediaDrm.ProvisionRequest
    ): ByteArray {
        return internalCallback.executeProvisionRequest(uuid, request)
    }

    override fun executeKeyRequest(
        uuid: UUID,
        request: ExoMediaDrm.KeyRequest
    ): ByteArray {
        val licenseUrl = request.licenseServerUrl
        logger.debug(TAG, "executeKeyRequest -> url=$licenseUrl")

        val token = tokenProvider()

        synchronized(internalCallback) {
            if (!token.isNullOrEmpty()) {
                val bearerToken = "Bearer $token"
                internalCallback.setKeyRequestProperty("Authorization", bearerToken)

                // Log del token inyectado usando tu logger
                logger.debug(TAG, "executeKeyRequest - Inyectando Header Authorization: $bearerToken")
            } else {
                internalCallback.clearKeyRequestProperty("Authorization")
                logger.error(TAG, "executeKeyRequest - TOKEN IS NULL OR EMPTY")
            }

            return try {
                val response = internalCallback.executeKeyRequest(uuid, request)
                logger.debug(TAG, "executeKeyRequest - EXITO: Petición de licencia completada")
                response
            } catch (e: Exception) {
                logger.error(TAG, "executeKeyRequest - ERROR: ${e.message}")
                throw e
            }
        }
    }

}