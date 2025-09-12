package com.mamm.mammapps.data.datasource.remote

import com.mamm.mammapps.data.di.BaseUrlApi
import com.mamm.mammapps.data.di.DeviceModelQualifier
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.di.LocatorApi
import com.mamm.mammapps.data.di.NoBaseUrlApi
import com.mamm.mammapps.data.di.NoBaseUrlNoRedirectApi
import com.mamm.mammapps.data.extension.toEPGRequestDate
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.epg.GetEPGResponse
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginRequest
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.model.playback.CLMRequest
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteDatasource @Inject constructor(
    @IdmApi private val idmApi: ApiService,
    @LocatorApi private val locatorApi: ApiService,
    @BaseUrlApi private val epgApi: ApiService,
    @NoBaseUrlApi private val noBaseUrlApi: ApiService,
    @NoBaseUrlNoRedirectApi private val clmApi : ApiService,
    @DeviceTypeQualifier private val deviceType: String,
    @DeviceSerialQualifier private val deviceSerial: String,
    @DeviceModelQualifier private val deviceModel: String,
    private val sessionManager: SessionManager,
    private val securePreferencesManager: SecurePreferencesManager
    ) {

    @Volatile
    private var cachedHomeContent: GetHomeContentResponse? = null

    @Volatile
    private var cachedMoviesContent: GetOtherContentResponse? = null

    suspend fun login(username: String, password: String): LoginResponse {
        return idmApi.login(LoginRequest(username, password, deviceType, deviceSerial))
    }

    suspend fun checkLocator(userName: String): LocatorResponse {
        return locatorApi.checkLocator(userName)
    }

    suspend fun getHomeContent(): GetHomeContentResponse {
        return withContext(Dispatchers.IO) {
            // Return cached content if available
            cachedHomeContent?.let { return@withContext it }

            val jsonFile = sessionManager.jsonFile

            require(jsonFile != null) {
                "JSON file is required to get Home Content, but was null"
            }

            val response = noBaseUrlApi.getHomeContent(jsonFile)

            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code()}: ${response.message()}")
            }

            val homeData = response.body()
                ?: throw IOException("Home content response body is null")

            cachedHomeContent = homeData
            homeData
        }
    }

     fun getCachedHomeContent(): GetHomeContentResponse? {
        return cachedHomeContent
    }

    suspend fun getChannelEPG(channelId: Int, date: LocalDate): GetEPGResponse {
        return withContext(Dispatchers.IO) {
            val response = epgApi.getEPG(channelId, date.toEPGRequestDate())

            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }

            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    suspend fun getMovies(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {

            cachedMoviesContent?.let { return@withContext it }

            val response = epgApi.getMovies(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val movieData = response.body() ?: throw IllegalStateException("Response body is null")

            cachedMoviesContent = movieData
            movieData
        }
    }

    fun getCachedMovies(): GetOtherContentResponse? {
        return cachedMoviesContent
    }

    suspend fun getUrlFromCLM(deliveryURL: String): String? {
        require(sessionManager.loginData?.skin?.operator != null
                && sessionManager.jwToken != null
                && securePreferencesManager.getCredentials().first != null) {
            "getUrlFromCLM requires loginData, jwToken and userName to be set"
        }

        val clmRequest = CLMRequest(
            user = securePreferencesManager.getCredentials().first!!,
            typeOfContentString = "",
            model = deviceModel,
            deviceType = deviceType,
            operator = sessionManager.loginData?.skin?.operator!!,
            jwt = sessionManager.jwToken!!
        )

        val fullUrl = if (deliveryURL.endsWith("/")) {
            "${deliveryURL}manifest.mpd"
        } else {
            "${deliveryURL}/manifest.mpd"
        }

        val response = clmApi.getUrlFromCLM(fullUrl, clmRequest)

        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()?.toResponseBody()
            throw HttpException(Response.error<Any>(response.code(), errorBody))
        }

        val locationHeader = response.headers()["location"]

        return locationHeader
    }

}