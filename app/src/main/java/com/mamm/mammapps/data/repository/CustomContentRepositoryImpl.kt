package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.domain.model.bookmark.Bookmark
import com.mamm.mammapps.domain.model.recommended.RecommendedContent
import com.mamm.mammapps.ui.model.CustomizedContent
import javax.inject.Inject

class CustomContentRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
    private val logger: Logger
) : CustomContentRepository {

    companion object {
        private const val TAG = "CustomContentRepositoryImpl"
    }

    override suspend fun getBookmarks(): Result<List<Bookmark>> {
        return runCatching {
            localDataSource.getBookmarks()?.map { it.toDomain() }?.let { return@runCatching it }
            remoteDatasource.getBookmarks().let { dtoList ->
                localDataSource.setBookmarks(dtoList)
                dtoList.map { it.toDomain() }
            }
        }.onFailure {
            logger.error(TAG, "getBookmarks failed: ${it.message}, $it")
        }
    }

    override suspend fun deleteBookmark(contentId: Int, contentType: String): Result<Unit> {
        return runCatching {
            remoteDatasource.deleteBookmark(
                contentId = contentId,
                contentType = contentType
            )
        }
    }

    override suspend fun getMostWatched(): Result<List<Any>> {
        return runCatching {
            localDataSource.getMostWatched()?.map { it.toDomain() }?.let { return@runCatching it }
            remoteDatasource.getMostWatched().let { dtoList ->
                localDataSource.setMostWatched(dtoList)
                dtoList.map { it.toDomain() }
            }
        }.onFailure {
            logger.error(TAG, "getMostWatched failed: ${it.message}, $it")
        }
    }

    override suspend fun getRecommended(): Result<List<Any>> {
        return runCatching {
            localDataSource.getRecommended()?.let { response ->
                return@runCatching (response.vods.orEmpty() + response.cutvs.orEmpty()).map { it.toDomain() }
            }

            remoteDatasource.getRecommended().let { response ->
                localDataSource.setRecommended(response)
                (response.vods.orEmpty() + response.cutvs.orEmpty()).map { it.toDomain() }
            }

        }.onFailure {
            logger.error(TAG, "getRecommended failed: ${it.message}, $it")
        }
    }

    override suspend fun getSimilar(subgenreId: Int): Result<RecommendedContent> {
        return runCatching {
            remoteDatasource.getSimilarContent(subgenreId = subgenreId).toDomain()
        }.onFailure {
            logger.error(TAG, "getSimilar failed: ${it.message}, $it")
        }
    }

    override fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>? {
        val content: Any? = when (contentType) {
            CustomizedContent.BookmarkType -> localDataSource.getBookmarks()?.find { it.id == contentId }?.toDomain()
            CustomizedContent.MostWatchedType -> localDataSource.getMostWatched()?.find { it.id == contentId }?.toDomain()
            CustomizedContent.RecommendedType -> {
                val recommended = localDataSource.getRecommended()
                val foundVod = recommended?.vods?.find { it.id == contentId }
                val foundCutv = recommended?.cutvs?.find { it.id == contentId }
                (foundVod ?: foundCutv)?.toDomain()
            }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override suspend fun searchContent (query: String): Result<List<Bookmark>> {
        return runCatching {
            remoteDatasource.search(query = query).map { it.toDomain() }
        }.onFailure {
            logger.error(TAG, "searchContent failed: ${it.message}, $it")
        }
    }

    override suspend fun getTopChannels(): Result<com.mamm.mammapps.domain.model.topchannels.TopChannels> {
        return runCatching {
            val response = remoteDatasource.getTopChannels()
            response.toDomain()
        }.onFailure {
            logger.error(TAG, "getTopChannels failed: ${it.message}, $it")
        }
    }

}