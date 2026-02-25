package com.mamm.mammapps.data.datasource.remote

import com.mamm.mammapps.data.di.StaticApi
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.sportsevent.SportsEventListDto
import com.mamm.mammapps.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SportsCalendarRemoteDatasource @Inject constructor(
    @StaticApi private val staticApi: ApiService,
    private val logger: Logger
) {

    suspend fun getFootballEvents(): SportsEventListDto {
        return withContext(Dispatchers.IO) {
            val response = staticApi.getFootballEvents()
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                logger.error("SportsEventsRemoteDatasource", "Error fetching football events: $errorBody")
                throw HttpException(response)
            }
            response.body() ?: throw IllegalStateException("Response body is null")
        }
    }
}
