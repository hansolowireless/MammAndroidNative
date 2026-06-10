package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.domain.interfaces.EPGRepository
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.epg.MultiDayEPG
import com.mamm.mammapps.domain.model.entity.Event
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.time.LocalDate
import javax.inject.Inject

class EPGRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDatasource,
    private val localDataSource: LocalDataSource,
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
            val homeContentDto = localDataSource.getHomeContent()
            val multiDayEPGMap = cachedMultiDayEPG?.multiDayEPG?.toMutableMap() ?: mutableMapOf()

            val epgChannelContentList = coroutineScope {
                homeContentDto?.channels
                    ?.mapNotNull { channelDto -> channelDto.id?.let { channelDto to it } }
                    ?.map { (channelDto, channelId) ->
                        async {
                            runCatching {
                                val epgResponseDto = remoteDataSource.getChannelEPG(channelId, date)
                                if (epgResponseDto.events?.isNotEmpty() == true) {
                                    EPGChannelContent(
                                        channel = channelDto.toDomain(),
                                        events = epgResponseDto.events.map { it.toDomain() }
                                    )
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

    override fun getLiveEventForChannel(channelId: Int): Event? {
        val todayEPG = cachedMultiDayEPG?.multiDayEPG?.get(LocalDate.now())
        return todayEPG?.find { it.channel.id == channelId }
            ?.events?.find { it.isLive() }
    }

    override fun findContent(channelId: Int, eventId: Int, date: LocalDate) : Event {
        val todayEPG = cachedMultiDayEPG?.multiDayEPG?.get(date)
        return todayEPG?.find { it.channel.id == channelId }
            ?.events?.find { it.getId() == eventId } ?: throw IllegalStateException("Content not found")
    }

    override fun clearCache() {
        cachedMultiDayEPG = null
    }
}