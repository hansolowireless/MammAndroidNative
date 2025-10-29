package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.remote.ApiServiceConstant
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicUrlInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        // Si la app no usa URLs dinámicas, no hacemos nada.
        if (!Config.shouldUseDynamicUrls) {
            return chain.proceed(request)
        }

        // Leemos la cabecera para saber qué URL usar
        val urlType = request.header(ApiServiceConstant.URL_TYPE_HEADER)

        val newUrlString = when (urlType) {
            ApiServiceConstant.URL_TYPE_BASE -> Config.baseUrl
            ApiServiceConstant.URL_TYPE_IDM -> Config.idmUrl
            ApiServiceConstant.URL_TYPE_SEARCH -> Config.searchUrl
            ApiServiceConstant.URL_TYPE_PASSWORD_RECOVERY -> Config.passwordRecoveryUrl
            else -> null // Si no hay cabecera, no se modifica la URL
        }

        if (newUrlString != null) {
            val newHttpUrl = newUrlString.toHttpUrlOrNull()
            if (newHttpUrl != null) {
                // Reconstruimos la URL de la petición manteniendo el path y los parámetros
                val finalUrl = request.url.newBuilder()
                    .scheme(newHttpUrl.scheme)
                    .host(newHttpUrl.host)
                    .port(newHttpUrl.port)
                    .build()

                // Creamos una nueva petición con la URL actualizada y eliminamos la cabecera
                request = request.newBuilder()
                    .removeHeader(ApiServiceConstant.URL_TYPE_HEADER) // La cabecera ya no es necesaria
                    .url(finalUrl)
                    .build()
            }
        }

        return chain.proceed(request)
    }
}
