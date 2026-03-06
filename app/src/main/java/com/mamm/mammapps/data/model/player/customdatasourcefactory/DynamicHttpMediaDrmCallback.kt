package com.mamm.mammapps.data.model.player.customdatasourcefactory

import com.google.android.exoplayer2.drm.ExoMediaDrm
import com.google.android.exoplayer2.drm.HttpMediaDrmCallback
import com.google.android.exoplayer2.drm.MediaDrmCallback
import com.google.android.exoplayer2.upstream.HttpDataSource
import java.util.UUID

class DynamicHttpMediaDrmCallback (
    defaultLicenseUrl: String,
    forceDefaultLicenseUrl: Boolean = true,
    dataSourceFactory: HttpDataSource.Factory,
    private val tokenProvider: () -> String?
) : MediaDrmCallback {

    private val internalCallback = HttpMediaDrmCallback(
        defaultLicenseUrl,
        forceDefaultLicenseUrl,
        dataSourceFactory
    )

    override fun executeProvisionRequest(uuid: UUID, request: ExoMediaDrm.ProvisionRequest): ByteArray {
        return internalCallback.executeProvisionRequest(uuid, request)
    }

    override fun executeKeyRequest(uuid: UUID, request: ExoMediaDrm.KeyRequest): ByteArray {
        val token = tokenProvider()
        if (!token.isNullOrEmpty()) {
            internalCallback.setKeyRequestProperty("Authorization", "Bearer $token")
        }
        return internalCallback.executeKeyRequest(uuid, request)
    }
}
