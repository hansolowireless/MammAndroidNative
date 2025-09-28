package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.session.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val sessionManager: SessionManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization", "Bearer ${sessionManager.jwToken}")
            .build()
        return chain.proceed(request)
    }
}