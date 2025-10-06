package com.mamm.mammapps.data.datasource.remote

import com.mamm.mammapps.data.cache.Cache
import com.mamm.mammapps.data.di.BaseUrlApi
import com.mamm.mammapps.data.di.CustomContentApi
import com.mamm.mammapps.data.di.DeviceModelQualifier
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.IdmApi
import com.mamm.mammapps.data.di.LocatorApi
import com.mamm.mammapps.data.di.NoBaseUrlApi
import com.mamm.mammapps.data.di.NoBaseUrlNoRedirectApi
import com.mamm.mammapps.data.di.QosApi
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.extension.isRedirect
import com.mamm.mammapps.data.extension.toEPGRequestDate
import com.mamm.mammapps.data.extension.transformData
import com.mamm.mammapps.data.local.SecurePreferencesManager
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetEPGResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginRequest
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.player.GetTickersResponse
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.data.model.player.heartbeat.HeartBeatRequest
import com.mamm.mammapps.data.model.player.playback.CLMRequest
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.remote.ApiService
import com.mamm.mammapps.ui.extension.toDate
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
    @DeviceModelQualifier private val deviceModel: String,
    private val sessionManager: SessionManager,
    private val securePreferencesManager: SecurePreferencesManager,
    private val cache: Cache,
    private val logger: Logger
) {

    @Volatile
    private var cachedHomeContent: GetHomeContentResponse? = null

    @Volatile
    private var cachedMoviesContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedDocumentariesContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedSportsContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedAdultsContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedKidsContent: GetOtherContentResponse? = null

    @Volatile
    private var cachedWarnerContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedAcontraContent: GetBrandedContentResponse? = null

    @Volatile
    private var cachedAMCContent: GetBrandedContentResponse? = null

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

            logger.debug(
                "getHomeContent", "JSON file: $jsonFile"
            )
            val response = noBaseUrlApi.getHomeContent(jsonFile)

            if (!response.isSuccessful) {
                throw IOException("HTTP ${response.code()}: ${response.message()}")
            }

            val homeData = response.body()
                ?: throw IOException("Home content response body is null")

            cachedHomeContent = homeData.transformData(
                channelOrder = sessionManager.channelOrder,
                userId = sessionManager.loginData?.userId.toString()
            )
            cachedHomeContent!!
        }
    }

    fun getCachedHomeContent(): GetHomeContentResponse? {
        return cachedHomeContent
    }

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

            cachedMoviesContent?.let { return@withContext it }

            val response = baseUrlApi.getMovies(jsonParam)
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

    //----------DOCUMENTARIES---------//
    suspend fun getDocumentaries(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {

            cachedDocumentariesContent?.let { return@withContext it }

            val response = baseUrlApi.getDocumentaries(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val docsData = response.body() ?: throw IllegalStateException("Response body is null")

            cachedDocumentariesContent = docsData
            docsData
        }
    }

    fun getCachedDocumentaries(): GetOtherContentResponse? {
        return cachedDocumentariesContent
    }

    //----------SPORTS---------//
    suspend fun getSports(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {
            cachedSportsContent?.let { return@withContext it }
            val response = baseUrlApi.getSports(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val sportsData = response.body() ?: throw IllegalStateException("Response body is null")
            cachedSportsContent = sportsData
            sportsData
        }
    }

    fun getCachedSports(): GetOtherContentResponse? {
        return cachedSportsContent
    }

    //----------KIDS---------//
    suspend fun getKids(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {

            cachedKidsContent?.let { return@withContext it }

            val response = baseUrlApi.getKids(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val kidsData = response.body() ?: throw IllegalStateException("Response body is null")

            cachedKidsContent = kidsData
            kidsData
        }
    }

    fun getCachedKids(): GetOtherContentResponse? {
        return cachedKidsContent
    }

    //----------ADULTS---------//
    suspend fun getAdults(jsonParam: String): GetOtherContentResponse {
        return withContext(Dispatchers.IO) {

            cachedAdultsContent?.let { return@withContext it }
            val response = baseUrlApi.getAdults(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val adultsData = response.body() ?: throw IllegalStateException("Response body is null")
            cachedAdultsContent = adultsData
            adultsData
        }
    }

    fun getCachedAdults(): GetOtherContentResponse? {
        return cachedAdultsContent
    }

    //----------WARNER---------//
    suspend fun getWarner(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {

            cachedWarnerContent?.let { return@withContext it }
            val response = baseUrlApi.getWarner(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val warnerData = response.body() ?: throw IllegalStateException("Response body is null")
            cachedWarnerContent = warnerData
            warnerData
        }
    }

    fun getCachedWarner(): GetBrandedContentResponse? {
        return cachedWarnerContent
    }

    //----------ACONTRA---------//
    suspend fun getAcontra(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {

            cachedAcontraContent?.let { return@withContext it }
            val response = baseUrlApi.getAcontra(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val acontraData =
                response.body() ?: throw IllegalStateException("Response body is null")
            cachedAcontraContent = acontraData
            acontraData
        }
    }

    fun getCachedAcontra(): GetBrandedContentResponse? {
        return cachedAcontraContent
    }

    //----------AMC---------//
    suspend fun getAMC(jsonParam: String): GetBrandedContentResponse {
        return withContext(Dispatchers.IO) {

            cachedAMCContent?.let { return@withContext it }
            val response = baseUrlApi.getAMC(jsonParam)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            val amcData = response.body() ?: throw IllegalStateException("Response body is null")
            cachedAMCContent = amcData
            amcData
        }
    }

    fun getCachedAMC(): GetBrandedContentResponse? {
        return cachedAMCContent
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
    suspend fun getUrlFromCLM(deliveryURL: String, typeOfContentString: String): String? {
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
            deviceType = deviceType,
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

    //----------USER IP---------//
    suspend fun getCurrentUserIp(): String {
        return try {
            getPublicIp() ?: "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1" // fallback
        }
    }

    private suspend fun getPublicIp(): String? {
        return try {
            withContext(Dispatchers.IO) {
                val url = URL("https://api.ipify.org?format=text")
                url.readText().trim()
            }
        } catch (e: Exception) {
            null
        }
    }

    //----------TICKERS---------//
    suspend fun getTickers(): GetTickersResponse {
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

    //----------BOOKMARKS---------//
    suspend fun getBookmarks() : List<Bookmark> {
        cache.getBookmarks()?.let {
            return it
        }
        val response = customContentApi.getBookmarks()
        cache.setBookmarks(response)
        return response
    }

    fun getCachedBookmarks() : List<Bookmark> {
        return cache.getBookmarks() ?: emptyList()
    }

    suspend fun saveBookmark(bookmarkRequest: SetBookmarkRequest) =
        customContentApi.setBookmark(bookmarkRequest)

    suspend fun deleteBookmark(contentId: Int, contentType: String) =
        customContentApi.deleteBookmark(
            contentId = contentId.toString(),
            contentType = contentType
        )

    //----------MOST WATCHED---------//
    suspend fun getMostWatched(): List<MostWatchedContent> {
        cache.getMostWatched()?.let {
            return it
        }
        val response = customContentApi.getMostWatched()
        cache.setMostWatched(response)
        return response
    }

    fun getCachedMostWatched() : List<MostWatchedContent> {
        return cache.getMostWatched() ?: emptyList()
    }

    //----------RECOMMENDED---------//
    suspend fun getRecommended() : GetRecommendedResponse {
        cache.getRecommended()?.let {
            return it
        }
        val response = customContentApi.getRecommended()
        cache.setRecommended(response)
        return response
    }

    fun getCachedRecommended() : GetRecommendedResponse? {
        return cache.getRecommended()
    }

    //----------SIMILAR CONTENT---------//
    suspend fun getSimilarContent(subgenreId: Int) : GetRecommendedResponse {
        val response = customContentApi.getSimilarContent(subgenreId)
        return response
    }

    //----------SEARCH---------//
    suspend fun search(query: String) : List<Bookmark> {
        return withContext(Dispatchers.IO) {
            val response = customContentApi.search(query)
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()?.toResponseBody()
                throw HttpException(Response.error<Any>(response.code(), errorBody))
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }

}