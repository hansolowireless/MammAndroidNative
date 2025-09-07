package com.mamm.mammapps.data.datasource.remote

import com.google.gson.Gson
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.EPGApi
import com.mamm.mammapps.data.di.HomeContentApi
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.di.LocatorApi
import com.mamm.mammapps.data.extension.toEPGRequestDate
import com.mamm.mammapps.data.model.GetEPGResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.LocatorResponse
import com.mamm.mammapps.data.model.LoginRequest
import com.mamm.mammapps.data.model.LoginResponse
import com.mamm.mammapps.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.HttpException
import retrofit2.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.time.LocalDate
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
    @IdmApi private val idmApi: ApiService,
    @LocatorApi private val locatorApi: ApiService,
    @EPGApi private val epgApi: ApiService,
    @HomeContentApi private val homeContentApi: ApiService,
    @DeviceTypeQualifier private val deviceType: String,
    @DeviceSerialQualifier private val deviceSerial: String
) {

    private var cachedHomeContent: GetHomeContentResponse? = null

    suspend fun login(username: String, password: String): LoginResponse {
        return idmApi.login(LoginRequest(username, password, deviceType, deviceSerial))
    }

    suspend fun checkLocator(userName: String): LocatorResponse {
        return locatorApi.checkLocator(userName)
    }

    suspend fun getHomeContent(url: String): GetHomeContentResponse {
        return withContext(Dispatchers.IO) {
            // Return cached content if available
            cachedHomeContent?.let { return@withContext it }

            val response = homeContentApi.getHomeContent(url)

            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code()}: ${response.message()}")
            }

            val homeData = response.body()
                ?: throw IOException("Home content response body is null")

            cachedHomeContent = homeData
            homeData // homeData ya es GetHomeContentResponse (no-nullable)
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

}