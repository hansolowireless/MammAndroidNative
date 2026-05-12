package com.mamm.mammapps.remote.interceptor

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.session.RefreshTokenRequest
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.remote.ApiService
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
    private val sessionDatsource: SessionDatasource,
    private val logger: Logger
) : Interceptor {

    companion object {
        private const val TAG = "AuthInterceptor"
        private const val REFRESH_TOKEN_PATH = "refresh-token"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val authenticatedRequest = originalRequest.newBuilder()
            .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_TYPE_BEARER${sessionDatsource.jwToken}")
            .build()

        val response = chain.proceed(authenticatedRequest)

        if ((response.code == HttpURLConnection.HTTP_UNAUTHORIZED || response.code == HttpURLConnection.HTTP_FORBIDDEN) &&
            !authenticatedRequest.url.encodedPath.contains(REFRESH_TOKEN_PATH)
        ) {

            synchronized(this) {
                val currentAccessToken = sessionDatsource.jwToken
                val requestToken = authenticatedRequest.header(AUTHORIZATION_HEADER)
                    ?.removePrefix(AUTHORIZATION_TYPE_BEARER)

                if (currentAccessToken != null && currentAccessToken != requestToken) {
                    response.close()
                    val retryRequest = originalRequest.newBuilder()
                        .header(AUTHORIZATION_HEADER, "$AUTHORIZATION_TYPE_BEARER$currentAccessToken")
                        .build()
                    return chain.proceed(retryRequest)
                }

                sessionDatsource.loginData?.refreshToken?.let { refreshTokenStr ->
                    runBlocking {
                        try {
                            remoteDatasource.get().refreshToken(RefreshTokenRequest(refreshTokenStr))
                        } catch (e: Exception) {
                            null
                        }
                    }?.let { refreshResponse ->
                        refreshResponse.data?.let { body ->
                            val newRefresh = body.refreshToken ?: refreshTokenStr
                            val newAccess = body.jwtoken

                            if (newAccess != null) {
                                sessionDatsource.updateToken(newRefresh, newAccess)
                                logger.info(TAG, "intercept - success refreshing and updating token")

                                response.close()
                                val newRequest = originalRequest.newBuilder()
                                    .header(
                                        AUTHORIZATION_HEADER,
                                        "$AUTHORIZATION_TYPE_BEARER$newAccess"
                                    )
                                    .build()
                                return chain.proceed(newRequest)
                            } else {
                                logger.error(
                                    TAG,
                                    "intercept - failed refreshing token, accessToken in response is null"
                                )
                            }
                        }
                    }

                }
            }
        }

        return response
    }
}
