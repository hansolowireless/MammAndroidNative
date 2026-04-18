package com.mamm.mammapps.data.datasource.remote

import androidx.core.net.toUri

import com.mamm.mammapps.data.di.BaseUrlApi
import com.mamm.mammapps.data.di.ChromecastDeviceTypeQualifier
import com.mamm.mammapps.data.di.CustomContentApi
import com.mamm.mammapps.data.di.DeviceModelQualifier
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.di.LocatorApi
import com.mamm.mammapps.data.di.NoBaseUrlApi
import com.mamm.mammapps.data.di.NoBaseUrlNoRedirectApi
import com.mamm.mammapps.data.di.QosApi
import com.mamm.mammapps.data.extension.correctAdultImages
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.extension.isRedirect
import com.mamm.mammapps.data.extension.toEPGRequestDate
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toGetHomeContentException
import com.mamm.mammapps.data.mapper.toGetMemoriesException
import com.mamm.mammapps.data.mapper.toLoginException
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetEPGResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.diagnostic.DiagResponseDto
import com.mamm.mammapps.domain.model.DownloadSpeedResult
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginRequest
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.model.memories.GetMemoriesResponse
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.player.GetTickersResponseDto
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.data.model.player.heartbeat.HeartBeatRequest
import com.mamm.mammapps.data.model.player.playback.CLMRequest
import com.mamm.mammapps.data.model.player.streamvx.StreamVxTokenRequest
import com.mamm.mammapps.data.model.player.streamvx.StreamVxTokenResponse
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.remote.ApiService
import com.mamm.mammapps.ui.extension.toDate
import com.mamm.mammapps.util.calculateMbps
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.URL
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDatasource @Inject constructor(
    @IdmApi private val idmApi: ApiService,
    @LocatorApi private val locatorApi: ApiService,
    @BaseUrlApi private val baseUrlApi: ApiService,
    @NoBaseUrlApi private val noBaseUrlApi: ApiService,
    @NoBaseUrlNoRedirectApi private val clmApi: ApiService,
    @QosApi private val qosApi: ApiService,
    @CustomContentApi private val customContentApi: ApiService,
    @DeviceTypeQualifier private val deviceType: String,
    @DeviceSerialQualifier private val deviceSerial: String,
    @ChromecastDeviceTypeQualifier private val ccastDeviceType: String,
    @DeviceModelQualifier private val deviceModel: String,
    private val sessionManager: SessionManager,
    private val securePreferencesManager: SecurePreferencesManager,
    private val logger: Logger
) {

    companion object {
        private const val BUFFER_SIZE = 8192
    }

    suspend fun login(username: String, password: String): LoginResponse {
        val response = idmApi.login(
            LoginRequest(
                username,
                password,
                deviceType,
                deviceSerial
            )
        )
        if (!response.isSuccessful) { throw response.code().toLoginException() }
        return response.body() ?: throw IllegalStateException("Response body is null")
    }

    suspend fun checkLocator(userName: String): LocatorResponse {
        return locatorApi.checkLocator(userName)
    }

    fun getOperatorLogoUrl(): String? {
        return sessionManager.operatorLogoUrl
    }

    suspend fun getHomeContent(): GetHomeContentResponse {
        return withContext(Dispatchers.IO) {
            val jsonFile = sessionManager.jsonFile

            require(jsonFile != null) {
                "JSON file is required to get Home Content, but was null"
            }

            logger.debug("getHomeContent", "JSON file: $jsonFile")
            val response = noBaseUrlApi.getHomeContent(jsonFile)

            if (!response.isSuccessful) {
                throw response.code().toGetHomeContentException()
            }

            val homeData = response.body()
                ?: throw IOException("Home content response body is null")

            homeData.transformData(
                channelOrder = sessionManager.channelOrder,
                userId = sessionManager.loginData?.userId.toString()
            )
        }
    }

    suspend fun getExpandedCategory(categoryId: Int): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {
            val jsonFile = sessionManager.jsonFile?.toUri()?.pathSegments?.lastOrNull()
            require(jsonFile != null) {
                "JSON file is required to get Home Content, but was null"
            }
            val response = baseUrlApi.getExpandCategory(categoryId.toString(), jsonFile)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }



    //----------EPG---------//
    suspend fun getChannelEPG(channelId: Int, date: LocalDate): GetEPGResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getEPG(channelId, date.toEPGRequestDate())
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------MOVIES---------//
    suspend fun getMovies(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getMovies(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------DOCUMENTARIES---------//
    suspend fun getDocumentaries(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getDocumentaries(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------SPORTS---------//
    suspend fun getSports(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getSports(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------KIDS---------//
    suspend fun getKids(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getKids(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------ADULTS---------//
    suspend fun getAdults(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {
            baseUrlApi.getAdults(jsonParam).let {
                if (!it.isSuccessful) {
                    val errorBody = it.errorBody()?.string()?.toResponseBody()
                    throw HttpException(Response.error<Any>(it.code(), errorBody))
                }
                val adultsData = it.body() ?: throw IllegalStateException("Response body is null")
                adultsData.correctAdultImages()
            }
        }
    }

    //----------WARNER---------//
    suspend fun getWarner(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getWarner(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------ACONTRA---------//
    suspend fun getAcontra(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getAcontra(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------AMC---------//
    suspend fun getAMC(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getAMC(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------SERIES - SEASON CONTENT---------//
    suspend fun getSeasonInfo(serieId: Int): GetSeasonInfoResponse {
        return withContext(Dispatchers.IO) {
            val response = baseUrlApi.getSeasonContent(serieId.toString())
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val seasonData = response.body() ?: throw IllegalStateException("Response body is null")
            seasonData
        }
    }


    //----------PLAYBACK---------//
    suspend fun getUrlFromCLM(
        deliveryURL: String,
        typeOfContentString: String,
        chromecast: Boolean = false
    ): String? {
        require(
            sessionManager.loginData?.skin?.operator != null
                    && sessionManager.jwToken != null
                    && securePreferencesManager.getCredentials().first != null
        ) {
            "getUrlFromCLM requires loginData, jwToken and userName to be set"
        }

        val clmRequest = CLMRequest(
            user = securePreferencesManager.getCredentials().first!!,
            typeOfContentString = typeOfContentString,
            model = deviceModel,
            deviceType = if (chromecast) ccastDeviceType else deviceType,
            operator = sessionManager.loginData?.skin?.operator!!,
            jwt = sessionManager.jwToken!!
        )

        var fullUrl = if (deliveryURL.endsWith("/")) {
            deliveryURL
        } else {
            "${deliveryURL}/"
        }

        fullUrl = fullUrl.plus("manifest.mpd")

        // Construir URL con parámetro sin nombre
        val queryParams = clmRequest.toQueryMap()
            .entries
            .joinToString("&") { "${it.key}=${it.value}" }

        val finalUrl = "$fullUrl?$typeOfContentString&$queryParams"

        val response = clmApi.getUrlFromCLM(finalUrl)

        if (!response.isSuccessful && !response.isRedirect()) {
            val errorBody = response.errorBody()?.string()?.toResponseBody()
            throw HttpException(Response.error<Any>(response.code(), errorBody))
        }

        val locationHeader = response.headers()["location"]

        return locationHeader
    }

    suspend fun sendHeartBeat() {
        val request = HeartBeatRequest(
            deviceType = deviceType,
            deviceSerial = deviceSerial
        )
        withContext(Dispatchers.IO) {
            val response = idmApi.sendHeartBeat(request)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()
                throw HttpException(response)
            }
        }
    }

    suspend fun sendQosData(data: QosData) {
        withContext(Dispatchers.IO) {
            val response = qosApi.sendQos(data)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()
                throw HttpException(response)
            }
        }
    }

    suspend fun getxToken(request: StreamVxTokenRequest): StreamVxTokenResponse {
        return withContext(Dispatchers.IO) {
            val response = idmApi.getStreamVxToken(request)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                logger.error("RemoteDatasource", "getStreamVxToken error: $errorBody")
                throw HttpException(response)
            }
            val body = response.body() ?: throw IllegalStateException("Response body is null")
            body
        }
    }

    //----------USER IP---------//
    suspend fun getCurrentUserIp(): String {
        return try {
            getPublicIp() ?: "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }

    private suspend fun getPublicIp(): String? {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://ips.service.openstream.es/?format=text")
                url.readText().trim()
            }
        } catch (e: Exception) {
            null
        }
    }

    //----------TICKERS---------//
    suspend fun getTickers(): GetTickersResponseDto {
        return withContext(Dispatchers.IO) {
            val url = "https://mammticker.b-cdn.net/" +
                    "${sessionManager.loginData?.userId}_tickets.json" +
                    "?t=${getCurrentDate().toDate().time}"

            val response = noBaseUrlApi.getTickers(url)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }

            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------TOP CHANNELS---------//
    suspend fun getTopChannels(): com.mamm.mammapps.data.model.topchannels.TopChannelsResponseDto {
        return withContext(Dispatchers.IO) {
            val email = securePreferencesManager.getCredentials().first ?: throw IllegalStateException("User email not found")
            val url = "https://masmedia.b-cdn.net/userdata/topchannels/$email.json"
            val response = noBaseUrlApi.getTopChannels(url)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------MEMORIES---------//
    suspend fun getMyMemories(): GetMemoriesResponse {
        return withContext(Dispatchers.IO) {
            val url = "https://masmedia.b-cdn.net/jsonmemories/user_" +
                    "${sessionManager.loginData?.userId}.json" +
                    "?t=${getCurrentDate().toDate().time}"

            val response = noBaseUrlApi.getMemories(url)
            if (!response.isSuccessful) {
                throw response.code().toGetMemoriesException()
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

    //----------BOOKMARKS---------//
    suspend fun getBookmarks(): List<Bookmark> {
        return customContentApi.getBookmarks()
    }

    suspend fun saveBookmark(type: String, contentId: Int, time: Long) {
        val bookmarkRequest = SetBookmarkRequest(
            type = type,
            contentId = contentId,
            time = time,
            userId = sessionManager.userId?.toIntOrNull()
        )
        customContentApi.setBookmark(bookmarkRequest)
    }

    suspend fun deleteBookmark(contentId: Int, contentType: String) =
        customContentApi.deleteBookmark(
            contentId = contentId.toString(),
            contentType = contentType
        )

    //----------MOST WATCHED---------//
    suspend fun getMostWatched(): List<MostWatchedContent> {
        return customContentApi.getMostWatched()
    }

    //----------RECOMMENDED---------//
    suspend fun getRecommended(): GetRecommendedResponse {
        return customContentApi.getRecommended()
    }

    //----------SIMILAR CONTENT---------//
    suspend fun getSimilarContent(subgenreId: Int): GetRecommendedResponse {
        val response = customContentApi.getSimilarContent(subgenreId)
        return response
    }

    //----------SEARCH---------//
    suspend fun search(query: String): List<Bookmark> {
        return withContext(Dispatchers.IO) {
            val response = customContentApi.search(query)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }



    //----------DIAGNOSTIC---------//
    suspend fun getDiagNodes(): DiagResponseDto {
        return withContext(Dispatchers.IO) {
            baseUrlApi.getDiagNodes().let {
                if (!it.isSuccessful) {
                    val errorBody = it.errorBody()?.string()?.toResponseBody()
                    throw HttpException(Response.error<Any>(it.code(), errorBody))
                } else {
                    it.body() ?: throw IllegalStateException("Response body is null")
                }
            }
        }
    }

    suspend fun performDownloadSpeedTest(url: String): Result<DownloadSpeedResult> = withContext(Dispatchers.IO) {
        runCatching {
            val response = noBaseUrlApi.downloadFile(url)
            val body = response.body()

            if (!response.isSuccessful || body == null) {
                throw Exception("Error al descargar el archivo: ${response.code()}")
            }

            val startTime = System.currentTimeMillis()
            var totalBytes: Long = 0

            // .use asegura que el stream se cierre al terminar
            body.byteStream().use { inputStream ->
                val buffer = ByteArray(BUFFER_SIZE)
                var bytesRead: Int
                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    totalBytes += bytesRead
                }
            }

            val durationMs = System.currentTimeMillis() - startTime
            val speedMbps = calculateMbps(totalBytes, durationMs)

            DownloadSpeedResult(
                speedMbps = speedMbps,
                durationMs = durationMs,
                bytesDownloaded = totalBytes
            )
        }
    }

}