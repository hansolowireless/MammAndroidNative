package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.bookmark.Bookmark
import com.mamm.mammapps.data.model.bookmark.Recommended
import com.mamm.mammapps.data.model.mostwatched.MostWatchedContent
import com.mamm.mammapps.data.model.recommended.GetRecommendedResponse
import com.mamm.mammapps.domain.interfaces.CustomContentRepository
import com.mamm.mammapps.ui.model.CustomizedContent
import javax.inject.Inject
import com.mamm.mammapps.data.mapper.toDomain

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
            localDataSource.getBookmarks()?.let { return@runCatching it }
            remoteDatasource.getBookmarks().let {
                localDataSource.setBookmarks(it)
                it
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

    override suspend fun getMostWatched(): Result<List<MostWatchedContent>> {
        return runCatching {
            localDataSource.getMostWatched()?.let { return@runCatching it }
            remoteDatasource.getMostWatched().let {
                localDataSource.setMostWatched(it)
                it
            }
        }.onFailure {
            logger.error(TAG, "getMostWatched failed: ${it.message}, $it")
        }
    }

    override suspend fun getRecommended(): Result<List<Recommended>> {
        return runCatching {
            localDataSource.getRecommended()?.let { response ->
                return@runCatching response.vods.orEmpty() + response.cutvs.orEmpty()
            }

            remoteDatasource.getRecommended().let {
                localDataSource.setRecommended(it)
                it.vods.orEmpty() + it.cutvs.orEmpty()
            }

        }.onFailure {
            logger.error(TAG, "getRecommended failed: ${it.message}, $it")
        }
    }

    override suspend fun getSimilar(subgenreId: Int): Result<GetRecommendedResponse> {
        return runCatching {
            remoteDatasource.getSimilarContent(subgenreId = subgenreId)
        }.onFailure {
            logger.error(TAG, "getSimilar failed: ${it.message}, $it")
        }
    }

    override fun findContent(
        contentId: Int,
        contentType: CustomizedContent
    ): Result<Any>? {
        val content: Any? = when (contentType) {
            CustomizedContent.BookmarkType -> localDataSource.getBookmarks()?.find { it.id == contentId }
            CustomizedContent.MostWatchedType -> localDataSource.getMostWatched()?.find { it.id == contentId }
            CustomizedContent.RecommendedType -> localDataSource.getRecommended()?.vods?.find { it.id == contentId } ?: localDataSource.getRecommended()?.cutvs?.find { it.id == contentId }
            else -> null
        }

        return content?.let { Result.success(it) }
    }

    override suspend fun searchContent (query: String): Result<List<Bookmark>> {
        return runCatching {
            remoteDatasource.search(query = query)
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