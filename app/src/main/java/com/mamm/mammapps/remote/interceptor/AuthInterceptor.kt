package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.session.RefreshTokenRequest
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.remote.ApiServiceConstant.AUTHORIZATION_HEADER
import com.mamm.mammapps.remote.ApiServiceConstant.AUTHORIZATION_TYPE_BEARER
import dagger.Lazy
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.net.HttpURLConnection
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val remoteDatasource: Lazy<RemoteDatasource>,
    private val sessionDatasource: SessionDatasource,
    private val logger: Logger
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val REFRESH_TOKEN_PATH = "refresh-token"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val authenticatedRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_TYPE_BEARER${sessionDatasource.jwToken}")
            .build()

        val response = chain.proceed(authenticatedRequest)

        if ((response.code == HttpURLConnection.HTTP_UNAUTHORIZED || response.code == HttpURLConnection.HTTP_FORBIDDEN) &&
            !authenticatedRequest.url.encodedPath.contains(REFRESH_TOKEN_PATH)
        ) {

            synchronized(this) {
                val currentAccessToken = sessionDatasource.jwToken
                val requestToken = authenticatedRequest.header(AUTHORIZATION_HEADER)
                    ?.removePrefix(AUTHORIZATION_TYPE_BEARER)

                if (currentAccessToken != null && currentAccessToken != requestToken) {
                    response.close()
                    val retryRequest = originalRequest.newBuilder()
                        .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_TYPE_BEARER$currentAccessToken")
                        .build()
                    return chain.proceed(retryRequest)
                }

                sessionDatasource.loginData?.refreshToken?.let { refreshTokenStr ->
                    runBlocking {
                        try {
                            remoteDatasource.get().refreshToken(RefreshTokenRequest(refreshTokenStr))
                        } catch (e: Exception) {
                            null
                        }
                    }?.let { refreshResponse ->
                        refreshResponse.data?.let { newLoginData ->
                            sessionDatasource.updateLoginData(newLoginData)
                            logger.info(TAG, "intercept - success refreshing and updating token")

                            response.close()
                            val newRequest = originalRequest.newBuilder()
                                .header(
                                    AUTHORIZATION_HEADER,
                                    "$AUTHORIZATION_TYPE_BEARER${newLoginData.jwtoken}"
                                )
                                .build()
                            return chain.proceed(newRequest)

                        }
                    }
                }
            }
        }

        return response
    }
}
