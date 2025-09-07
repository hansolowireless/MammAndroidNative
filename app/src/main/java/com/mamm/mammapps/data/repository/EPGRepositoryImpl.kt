package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.epg.MultiDayEPG
import com.mamm.mammapps.domain.interfaces.EPGRepository
import java.time.LocalDate
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope


class EPGRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDatasource,
    private val logger: Logger
) : EPGRepository {

    companion object {
        private const val TAG = "EPGRepositoryImpl"
    }

    private var cachedMultiDayEPG : MultiDayEPG? = null

    override suspend fun getEPG(date: LocalDate): Result<List<EPGChannelContent>> {
        return runCatching {
            cachedMultiDayEPG?.multiDayEPG?.get(date)?.let { cachedData ->
                logger.debug(TAG, "EPG data already cached for date: $date")
                return@runCatching cachedData
            }

            loadEPGFromAPI(date).getOrThrow()

            cachedMultiDayEPG?.multiDayEPG?.get(date) ?: emptyList()
        }
    }

    private suspend fun loadEPGFromAPI(date: LocalDate): Result<Unit> {
        return runCatching {
            val homeContent = remoteDataSource.getCachedHomeContent()
            val multiDayEPGMap = cachedMultiDayEPG?.multiDayEPG?.toMutableMap() ?: mutableMapOf()

            val epgChannelContentList = coroutineScope {
                homeContent?.channels
                    ?.mapNotNull { channel -> channel.id?.let { channel to it } }
                    ?.map { (channel, channelId) ->
                        async {
                            runCatching {
                                val epgResponse = remoteDataSource.getChannelEPG(channelId, date)
                                if (epgResponse.events?.isNotEmpty() == true) {
                                    EPGChannelContent(channel = channel, events = epgResponse.events)
                                } else null
                            }.onFailure { error ->
                                logger.error(TAG, "Error obteniendo EPG para canal $channelId: ${error.message}")
                            }.getOrNull()
                        }
                    }
                    ?.awaitAll()
                    ?.filterNotNull()
                    ?: emptyList()
            }

            multiDayEPGMap[date] = epgChannelContentList
            cachedMultiDayEPG = MultiDayEPG(multiDayEPGMap)
        }
    }
}