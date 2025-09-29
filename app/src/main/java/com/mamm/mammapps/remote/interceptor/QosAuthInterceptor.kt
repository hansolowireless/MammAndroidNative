package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class QosAuthInterceptor(
    private val sessionManager: SessionManager,
    private val securePreferencesManager: SecurePreferencesManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader(
                "Authorization",
                "Bearer ${sessionManager.loginData?.skin?.operator + ',' + securePreferencesManager.getUsername()}"
            )
            .build()
        return chain.proceed(request)
    }
}