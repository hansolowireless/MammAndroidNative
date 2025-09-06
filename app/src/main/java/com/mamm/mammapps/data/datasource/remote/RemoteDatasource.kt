package com.mamm.mammapps.data.datasource.remote

import com.google.gson.Gson
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.di.LocatorApi
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
import javax.inject.Inject

class RemoteDatasource @Inject constructor(
    @IdmApi private val idmApi: ApiService,
    @LocatorApi private val locatorApi: ApiService,
    @DeviceTypeQualifier private val deviceType: String,
    @DeviceSerialQualifier private val deviceSerial: String
) {
    suspend fun login(username: String, password: String): LoginResponse {
        return idmApi.login(LoginRequest(username, password, deviceType, deviceSerial))
    }

    suspend fun checkLocator(userName: String): LocatorResponse {
        return locatorApi.checkLocator(userName)
    }


    suspend fun getHomeContent(url: String): GetHomeContentResponse {
        return withContext(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = okhttp3.Request.Builder()
                .url(url)
                .get()
                .build()

            val response = client.newCall(request).execute()

            if (!response.isSuccessful) {
                val errorBody = response.body?.string()?.toResponseBody()
                    ?: "".toResponseBody()
                throw HttpException(Response.error<Any>(response.code, errorBody))
            }

            val responseBody = response.body.string()

            Gson().fromJson(responseBody, GetHomeContentResponse::class.java)
        }
    }
}