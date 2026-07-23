package com.mamm.mammapps.domain.interfaces

import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.Genre
import com.mamm.mammapps.domain.model.BrandedContent
import com.mamm.mammapps.domain.model.HomeContent
import com.mamm.mammapps.domain.model.OtherContent
import com.mamm.mammapps.domain.model.Subgenre
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.memories.Memories
import com.mamm.mammapps.domain.model.serie.SerieInfo
import com.mamm.mammapps.ui.model.ContentIdentifier

interface MammRepository {

    suspend fun getHomeContent() : Result<HomeContent>

    suspend fun getOperatorFeatured() : Result<List<Featured>>
    suspend fun getMovies(): Result<OtherContent>
    suspend fun getAdults(): Result<BrandedContent>
    suspend fun getDocumentaries(): Result<OtherContent>
    suspend fun getKids(): Result<OtherContent>
    suspend fun getSports(): Result<OtherContent>

    suspend fun getWarner(): Result<BrandedContent>
    suspend fun getAcontra(): Result<BrandedContent>
    suspend fun getAMC(): Result<BrandedContent>
    suspend fun getMemories () : Result<Memories>

    suspend fun getSeasonsInfo(serieId: Int): Result<SerieInfo>
    suspend fun getExpandedCategoryContent(categoryId: Int): Result<BrandedContent>

    fun findHomeContent(identifier: ContentIdentifier): Result<Any>?
    fun findMovieContent(identifier: ContentIdentifier): Result<Any>?
    fun findDocumentaryContent(identifier: ContentIdentifier): Result<Any>?
    fun findAdultContent(identifier: ContentIdentifier): Result<Any>?
    fun findKidsContent(identifier: ContentIdentifier): Result<Any>?
    fun findSportsContent(identifier: ContentIdentifier): Result<Any>?
    fun findWarnerContent(identifier: ContentIdentifier): Result<Any>?
    fun findAcontraContent(identifier: ContentIdentifier): Result<Any>?
    fun findAMCContent(identifier: ContentIdentifier): Result<Any>?
    fun findMemoriesContent(identifier: ContentIdentifier) : Result<Any>?

    fun findGenreWithId(id: Int): Result<Genre>
    fun findChannelWithId(id: Int): Result<Channel>

    fun shouldRequestPin(): Boolean
    fun validatePin(pin: String): Boolean
    fun savePinSuccessTimestamp()

    fun getSubgenreList() : Result<List<Subgenre>>
}