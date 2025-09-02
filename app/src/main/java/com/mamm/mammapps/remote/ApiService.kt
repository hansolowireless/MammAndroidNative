package com.mamm.mammapps.remote

import com.mamm.mammapps.data.model.LocatorResponse
import com.mamm.mammapps.data.model.LoginRequest
import com.mamm.mammapps.data.model.LoginResponse
import retrofit2.http.*

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
}