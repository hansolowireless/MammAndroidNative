package com.mamm.mammapps.data.model.player.customdatasourcefactory

import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.upstream.DefaultLoadErrorHandlingPolicy
import com.google.android.exoplayer2.upstream.LoadErrorHandlingPolicy

/**
 * Política de errores de carga que no reintenta los [TokenRejectedException].
 *
 * TokenParamDataSourceFactory ya reintenta cada petición rechazada (regenerando el stoken y
 * refrescando la IP). Si aun así la CDN la rechaza, reintentar con la misma clave no sirve:
 * con la política por defecto, si el player no consume el buffer (pausado, deco en reposo), el
 * refresco del manifiesto de un directo se reintentaba cada 5 s para siempre (p. ej. al rotar
 * backend la clave de firma de noche) sin que el error llegara nunca al player. Al marcarlo
 * como fatal se deja de pedir y, en cuanto el player necesita datos, llega a onPlayerError y el
 * ViewModel renueva la clave pidiendo de nuevo la URL al CLM.
 */
class TokenRejectedLoadErrorHandlingPolicy : DefaultLoadErrorHandlingPolicy() {

    override fun getRetryDelayMsFor(loadErrorInfo: LoadErrorHandlingPolicy.LoadErrorInfo): Long {
        if (loadErrorInfo.exception is TokenRejectedException) return C.TIME_UNSET
        return super.getRetryDelayMsFor(loadErrorInfo)
    }
}
