package com.mamm.mammapps.remote

import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.epg.GetEPGResponse
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginRequest
import com.mamm.mammapps.data.model.login.LoginResponse
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


//    // ---------- Bookmarks ----------
//    @GET("keep-watching/get-marks")
//    suspend fun getBookmarks(): List<ContentEntity>
//
//    @POST("bookmark/add")
//    suspend fun addBookmark(
//        @Body bookmark: BookmarkRequest
//    ): ApiResponse
//
//    @POST("bookmark/delete")
//    suspend fun deleteBookmark(
//        @Body bookmark: BookmarkRequest
//    ): ApiResponse
//
//    // ---------- Search ----------
//    @GET("search")
//    suspend fun searchContent(
//        @Query("q") query: String
//    ): SearchResponse
//
//    // ---------- Metrics / QoS ----------
//    @POST("QosMonitor/logs")
//    suspend fun sendQosLogs(
//        @Body logs: QosLogRequest
//    ): ApiResponse

    // ---------- Playback ----------
    @GET
    @Headers(
        "Content-Type: application/json",
        "Accept: application/json"
    )
    suspend fun getUrlFromCLM(
        @Url url: String
    ): Response<String>

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