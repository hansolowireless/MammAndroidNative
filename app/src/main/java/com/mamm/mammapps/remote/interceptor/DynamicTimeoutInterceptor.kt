package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.remote.ApiServiceConstant.TIMEOUT_HEADER
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DynamicTimeoutInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val timeoutHeader = request.header(TIMEOUT_HEADER)

        return if (timeoutHeader != null) {
            val timeout = timeoutHeader.toLongOrNull() ?: 30000L // Default si falla el parseo

            val newChain = chain
                .withConnectTimeout(timeout.toInt(), TimeUnit.MILLISECONDS)
                .withReadTimeout(timeout.toInt(), TimeUnit.MILLISECONDS)
                .withWriteTimeout(timeout.toInt(), TimeUnit.MILLISECONDS)

            val newRequest = request.newBuilder()
                .removeHeader(TIMEOUT_HEADER)
                .build()

            newChain.proceed(newRequest)
        } else {
            // Si no hay cabecera, proceed normal con los tiempos del OkHttpClient
            chain.proceed(request)
        }
    }
}