package com.mamm.mammapps.remote

import com.mamm.mammapps.data.model.GetBrandedContentResponseDto
import com.mamm.mammapps.data.model.GetEPGResponseDto
import com.mamm.mammapps.data.model.GetHomeContentResponseDto
import com.mamm.mammapps.data.model.GetOtherContentResponseDto
import com.mamm.mammapps.data.model.bookmark.BookmarkDto
import com.mamm.mammapps.data.model.bookmark.SetBookmarkRequest
import com.mamm.mammapps.data.model.diagnostic.DiagResponseDto
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginRequest
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.data.model.memories.GetMemoriesResponseDto
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContentDto
import com.mamm.mammapps.data.model.player.GetTickersResponseDto
import com.mamm.mammapps.data.model.player.TickerDto
import com.mamm.mammapps.data.model.player.QosDataDto
import com.mamm.mammapps.data.model.player.heartbeat.HeartBeatRequest
import com.mamm.mammapps.data.model.player.streamvx.StreamVxTokenRequest
import com.mamm.mammapps.data.model.player.streamvx.StreamVxTokenResponse
import com.mamm.mammapps.data.model.session.RefreshTokenRequest
import com.mamm.mammapps.data.model.session.RefreshTokenResponse
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponseDto
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponseDto
import com.mamm.mammapps.data.model.sportsevent.SportsEventListDto
import com.mamm.mammapps.data.model.loginwithcode.LoginCodeGenerateRequest
import com.mamm.mammapps.data.model.loginwithcode.LoginCodeGenerateResponseDto
import com.mamm.mammapps.data.model.loginwithcode.LoginCodeStatusResponseDto
import com.mamm.mammapps.data.model.loginwithcode.AuthLoginCodeRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming
import retrofit2.http.Url

/*En los header se especifica el tipo de URL
* para que la transforme el dynamic url interceptor
*/
interface ApiService {

    // ---------- IDM ----------
    @POST("aaservice/login")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_IDM}"
    )
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    // ---------- LOCATOR ----------
    @GET("locator/endpoint")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun checkLocator(
        @Query("login") userName: String
    ): LocatorResponse

    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getHomeContent(@Url url: String): Response<GetHomeContentResponseDto>

    // ---------- EPG ----------
    @GET("epg_files/EPG_{channelID}_{date}.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getEPG(
        @Path("channelID") channelID: Int,
        @Path("date") date: String
    ): Response<GetEPGResponseDto>


    // ---------- MOVIES ----------
    @GET("epg_files/cine_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getMovies(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponseDto>

    // ---------- DOCUMENTARIES ----------
    @GET("epg_files/doc_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getDocumentaries(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponseDto>

    // ---------- SPORTS ----------
    @GET("epg_files/dep_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getSports(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponseDto>

    // ---------- KIDS ----------
    @GET("epg_files/inf_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getKids(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponseDto>

    // ---------- ADULTS ----------
    @GET("epg_files/adt_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getAdults(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponseDto>

    // ---------- WARNER ----------
    @GET("epg_files/wb_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getWarner(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponseDto>

    // ---------- ACONTRA ----------
    @GET("epg_files/acf_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getAcontra(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponseDto>

    // ---------- AMC ----------
    @GET("epg_files/amc_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getAMC(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponseDto>

    // ---------- SEASON CONTENT ----------
    @GET("epg_files/serie_{serieId}.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_BASE}"
    )
    suspend fun getSeasonContent(
        @Path("serieId") jsonParam: String
    ): Response<GetSeasonInfoResponseDto>

    // ---------- Playback ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getUrlFromCLM(
        @Url url: String
    ): Response<String>

    @POST("/aaservice/pushsession")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_IDM}"
    )
    suspend fun sendHeartBeat(
        @Body heartBeatRequest: HeartBeatRequest
    ): Response<Unit>

    @POST("/aaservice/refresh-token")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_IDM}"
    )
    suspend fun refreshToken(
        @Body refreshToken: RefreshTokenRequest
    ): Response<RefreshTokenResponse>

    @POST("/aaservice/streamvxtoken")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_IDM}",
        "${ApiServiceConstant.TIMEOUT_HEADER}: ${ApiServiceConstant.STREAMVX_TIMEOUT_VALUE}"
    )
    suspend fun getStreamVxToken(
        @Body request: StreamVxTokenRequest
    ): Response<StreamVxTokenResponse>

    @POST("/QosMonitor/logs")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun sendQos(
        @Body qosRequest: QosDataDto
    ): Response<Unit>

    // ---------- Bookmarks ----------
    @GET("keep-watching/get-marks")
    suspend fun getBookmarks(): List<BookmarkDto>

    @POST("keep-watching/set")
    suspend fun setBookmark(
        @Body bookmark: SetBookmarkRequest
    ): Response<Unit>

    @POST("keep-watching/delete-mark/{contentId}/{contentType}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun deleteBookmark(
        @Path("contentId") contentId: String,
        @Path("contentType") contentType: String,
    ): Response<Unit>

    //---------Most Watched---------------
    @GET("recommendation/get-most-watched")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getMostWatched(): List<MostWatchedContentDto>

    //---------Recommended---------------
    @GET("recommendation/get-recommendations")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getRecommended(): GetRecommendedResponseDto

    //---------Similar Content---------------
    @GET("recommendation/get-similar")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getSimilarContent(
        @Query("subgenre") subgenreId: Int
    ): GetRecommendedResponseDto

    // ---------- Search ----------
    @GET("/content/search")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json",
        "${ApiServiceConstant.URL_TYPE_HEADER}:${ApiServiceConstant.URL_TYPE_SEARCH}"
    )
    suspend fun search(
        @Query("chain") searchQuery: String
    ): Response<List<BookmarkDto>>

    // ---------- Tickers ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getTickers(
        @Url url: String
    ): Response<GetTickersResponseDto>

    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getChannelTicker(
        @Url url: String
    ): Response<TickerDto>

    // ---------- Memories ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getMemories(
        @Url url: String
    ): Response<GetMemoriesResponseDto>

    // ------- Expand Category ----------
    @GET("epg_files/cat_{categoryId}_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getExpandCategory(
        @Path("categoryId") categoryId: String,
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponseDto>

    // ------- Diagnostics ----------
    @GET("diag.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getDiagNodes(
    ): Response<DiagResponseDto>

    @Streaming
    @GET
    suspend fun downloadFile(@Url url: String): Response<ResponseBody>

    @GET("futbol/xml/calendario_futbol_completo.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getFootballEvents(): Response<SportsEventListDto>

    // ---------- Top Channels ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getTopChannels(
        @Url url: String
    ): Response<com.mamm.mammapps.data.model.topchannels.TopChannelsResponseDto>

    // ---------- TV Code ----------
    @POST("locator/tv-code/generate")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun generateTvCode(
        @Body request: LoginCodeGenerateRequest
    ): Response<LoginCodeGenerateResponseDto>

    @GET("locator/tv-code/status/{code}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun checkTvCodeStatus(
        @Path("code") code: String
    ): Response<LoginCodeStatusResponseDto>

    @POST("locator/tv-code/auth")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun authLoginCode(
        @Body request: AuthLoginCodeRequest
    ): Response<Unit>
}