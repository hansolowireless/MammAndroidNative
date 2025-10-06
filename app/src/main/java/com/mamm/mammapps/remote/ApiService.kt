package com.mamm.mammapps.remote

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
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {

    // ---------- IDM ----------
    @POST("aaservice/login")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

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
    suspend fun getHomeContent(@Url url: String): Response<GetHomeContentResponse>

    // ---------- EPG ----------
    @GET("epg_files/EPG_{channelID}_{date}.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getEPG(
        @Path("channelID") channelID: Int,
        @Path("date") date: String
    ): Response<GetEPGResponse>


    // ---------- MOVIES ----------
    @GET("epg_files/cine_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getMovies(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponse>

    // ---------- DOCUMENTARIES ----------
    @GET("epg_files/doc_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getDocumentaries(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponse>

    // ---------- SPORTS ----------
    @GET("epg_files/dep_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getSports(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponse>

    // ---------- KIDS ----------
    @GET("epg_files/inf_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getKids(
        @Path("jsonParam") jsonParam: String
    ): Response<GetOtherContentResponse>

    // ---------- ADULTS ----------
    @GET("epg_files/adt_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getAdults(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponse>

    // ---------- WARNER ----------
    @GET("epg_files/wb_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getWarner(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponse>

    // ---------- ACONTRA ----------
    @GET("epg_files/acf_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getAcontra(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponse>

    // ---------- AMC ----------
    @GET("epg_files/amc_pkg_{jsonParam}")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getAMC(
        @Path("jsonParam") jsonParam: String
    ): Response<GetBrandedContentResponse>

    // ---------- SEASON CONTENT ----------
    @GET("epg_files/serie_{serieId}.json")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getSeasonContent(
        @Path("serieId") jsonParam: String
    ): Response<GetSeasonInfoResponse>

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
        "Accept: application/json"
    )
    suspend fun sendHeartBeat(
        @Body heartBeatRequest: HeartBeatRequest
    ): Response<Unit>

    @POST("/QosMonitor/logs")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun sendQos(
        @Body qosRequest: QosData
    ): Response<Unit>

    // ---------- Bookmarks ----------
    @GET("keep-watching/get-marks")
    suspend fun getBookmarks(): List<Bookmark>

    @POST("bookmark/set")
    suspend fun setBookmark(
        @Body bookmark: SetBookmarkRequest
    ): Response<Unit>

    @POST("bookmark/{contentId}/{contentType}")
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
    suspend fun getMostWatched(): List<MostWatchedContent>

    //---------Recommended---------------
    @GET("recommendation/get-recommendations")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getRecommended(): GetRecommendedResponse

    //---------Similar Content---------------
    @GET("recommendation/get-similar")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getSimilarContent(
        @Query("subgenre") subgenreId: Int
    ): GetRecommendedResponse

    // ---------- Search ----------
    @GET("/content/search")
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun search(
        @Query("chain") searchQuery: String
    ): Response<List<Bookmark>>

    // ---------- Tickers ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getTickers(
        @Url url: String
    ): Response<GetTickersResponse>



}