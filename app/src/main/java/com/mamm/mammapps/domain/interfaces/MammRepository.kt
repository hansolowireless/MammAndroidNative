package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetBrandedContentResponse
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.serie.GetSeasonInfoResponse
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.ui.model.ContentIdentifier

interface MammRepository {

    suspend fun getHomeContent() : Result<GetHomeContentResponse>
    suspend fun getMovies(): Result<GetOtherContentResponse>
    suspend fun getAdults(): Result<GetOtherContentResponse>
    suspend fun getDocumentaries(): Result<GetOtherContentResponse>
    suspend fun getKids(): Result<GetOtherContentResponse>
    suspend fun getSports(): Result<GetOtherContentResponse>

    suspend fun getWarner(): Result<GetBrandedContentResponse>
    suspend fun getAcontra(): Result<GetBrandedContentResponse>
    suspend fun getAMC(): Result<GetBrandedContentResponse>

    suspend fun getSeasonsInfo(serieId: Int): Result<GetSeasonInfoResponse>

    fun findHomeContent(identifier: ContentIdentifier): Result<Any>?
    fun findMovieContent(identifier: ContentIdentifier): Result<Any>?
    fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>?
    fun findAdultContent(identifier: ContentIdentifier): Result<Any>?
    fun findKidsContent(identifier: ContentIdentifier): Result<Any>?
    fun findSportsContent(identifier: ContentIdentifier): Result<Any>?
    fun findWarnerContent(identifier: ContentIdentifier): Result<Any>?
    fun findAcontraContent(identifier: ContentIdentifier): Result<Any>?
    fun findAMCContent(identifier: ContentIdentifier): Result<Any>?

    fun findGenreWithId(id: Int): Result<Genre>
    fun findChannelWithId(id: Int): Result<Channel>
}