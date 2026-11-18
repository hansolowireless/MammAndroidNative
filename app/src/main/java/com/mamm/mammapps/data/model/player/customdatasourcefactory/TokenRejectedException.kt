package com.mamm.mammapps.data.model.player.customdatasourcefactory

import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.HttpDataSource

/**
 * La CDN ha rechazado el token (401/403) incluso tras los reintentos de TokenParamDataSourceFactory.
 * Lleva el 401/403 original como causa y el mismo código de error que él (2004).
 */
class TokenRejectedException(
    cause: HttpDataSource.InvalidResponseCodeException,
    dataSpec: DataSpec
) : HttpDataSource.HttpDataSourceException(
    "Token rechazado por la CDN (${cause.responseCode})",
    cause,
    dataSpec,
    PlaybackException.ERROR_CODE_IO_BAD_HTTP_STATUS,
    TYPE_OPEN
)
