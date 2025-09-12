package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.data.model.Genre
import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.GetOtherContentResponse
import com.mamm.mammapps.data.model.login.LocatorResponse
import com.mamm.mammapps.data.model.login.LoginResponse
import com.mamm.mammapps.ui.model.ContentIdentifier

interface MammRepository {
    suspend fun login(username: String, password: String): Result<LoginResponse>
    suspend fun checkLocator(username: String): Result<LocatorResponse>
    suspend fun saveUserCredentials(username: String, password: String) : Result<Unit>
    suspend fun getUserCredentials(): Result<Pair<String?, String?>>
    suspend fun getHomeContent() : Result<GetHomeContentResponse>
    suspend fun getMovies(jsonParam: String): Result<GetOtherContentResponse>
    fun findHomeContent(identifier: ContentIdentifier): Result<Any>?
    fun findMovieContent(identifier: ContentIdentifier): Result<Any>?
    fun findGenreWithId(id: Int): Result<Genre>
}