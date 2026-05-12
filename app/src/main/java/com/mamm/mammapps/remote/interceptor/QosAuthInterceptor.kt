package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.local.SharedPreferencesManager
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import okhttp3.Interceptor
import okhttp3.Response

class QosAuthInterceptor(
    private val sessionManager: SessionDatasource,
    private val securePreferencesManager: SharedPreferencesManager
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