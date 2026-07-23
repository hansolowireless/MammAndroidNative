package com.mamm.mammapps.data.repository

import android.util.Base64
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.mapper.toDomain
import com.mamm.mammapps.data.mapper.toDto
import com.mamm.mammapps.domain.model.exception.TickerException
import com.mamm.mammapps.data.datasource.session.SessionDatasource
import com.mamm.mammapps.data.util.DrmAuthUtil
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.model.player.QosData
import com.mamm.mammapps.domain.model.player.Ticker
import com.mamm.mammapps.domain.model.player.VastAdParameters
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import kotlinx.coroutines.flow.Flow
import java.time.Duration
import javax.inject.Inject

class PlaybackRepositoryImpl @Inject constructor(
    private val remoteDatasource: RemoteDatasource,
    private val localDatasource: LocalDataSource,
    private val sessionDatasource: SessionDatasource,
    private val logger: Logger
) : PlaybackRepository {

    companion object {
        private const val TAG = "PlaybackRepositoryImpl"
        private const val JSON_KEY_USER_ID = "\"UserID\""
        private const val JSON_KEY_STREAM_NAME = "\"StreamName\""
        private const val JSON_KEY_OPERATOR = "\"Operator\""
        private const val JSON_KEY_AUTH_STRING = "\"AuthenticationString\""
    }

    override suspend fun getVideoUrlFromCLM(
        deliveryURL: String,
        typeOfContentString: String,
        chromecast: Boolean
    ): Result<String> {
        return runCatching {
            val locationUrl = remoteDatasource.getUrlFromCLM(
                deliveryURL = deliveryURL,
                typeOfContentString = typeOfContentString,
                chromecast = chromecast
            )
            logger.debug(TAG, "getVideoUrlFromCLM - $locationUrl")
            locationUrl ?: throw Exception("Location header not found in response")
        }
    }

    override suspend fun getDRMUrl(
        content: ContentToPlayUI
    ): Result<Pair<String, String>> {
        return runCatching {
            val userName = sessionDatasource.getUserCredentials().first
            val token = sessionDatasource.loginData?.token
            val userID = sessionDatasource.loginData?.userId
            val operatorName = Config.operatorNameDRM
            val deviceType = localDatasource.getDeviceType()
            val streamID = content.epgEventInfo?.fatherChannelId ?: content.identifier.getIdValue()

            val drmPayload = DrmAuthUtil.generateAuthPayload(
                eventType = content.getDRMString(),
                deviceTypeStr = deviceType,
                userName = userName ?: "",
                deviceSerial = localDatasource.getDeviceSerial(),
                sessionToken = token,
                drmSecretKey64 = localDatasource.getDrmSecretKey64(),
                drmiV64 = localDatasource.getDrmiV64(),
                expireDuration = Duration.ZERO,
                urlEncode = true,
                base64BeforeAes = true
            )

            // License URL construction (always widevine for Android)
            val licenseURL =
                (content.drmUrl) ?: ("${localDatasource.getDrmBaseUrl()}widevine/getLicense?" +
                        "userID=$userID&" +
                        "authenticationString=$drmPayload&" +
                        "streamName=$streamID&" +
                        "operator=$operatorName")

            logger.debug(TAG, "getDRMUrl - $licenseURL")

            val customDataJson = "{\n" +
                    "                $JSON_KEY_USER_ID:\"$userID\",\n" +
                    "                $JSON_KEY_STREAM_NAME:\"$streamID\",\n" +
                    "                $JSON_KEY_OPERATOR:\"$operatorName\",\n" +
                    "                $JSON_KEY_AUTH_STRING:\"$drmPayload\"\n" +
                    "                }"

            val customDataEncoded = Base64.encodeToString(customDataJson.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

            Pair(licenseURL, customDataEncoded)

        }.onFailure { exception ->
            logger.error(TAG, "error getting DRM URL $exception.message")

        }
    }

    override suspend fun getVastAdParameters(): Result<VastAdParameters> {
        return runCatching {
            VastAdParameters(
                operator = sessionDatasource.operator ?: Config.operatorNameDRM,
                userId = sessionDatasource.loginData?.userId ?: 0,
                device = localDatasource.getDeviceSerial(),
                deviceType = localDatasource.getDeviceType()
            )
        }.onFailure { exception ->
            logger.error(TAG, "error getting VAST ad parameters: ${exception.message}")
        }
    }

    // Ticker en tiempo real por canal: endpoint dinámico del ticker app
    override suspend fun getChannelTicker(channel: Int, type: String): Result<Ticker?> {
        return runCatching {
            val tickerUrl = Config.tickerUrl ?: throw TickerException.MissingData
            val url = buildChannelTickerUrl(tickerUrl, channel, type)
            remoteDatasource.getChannelTicker(url)?.toDomain()
        }.onFailure {
            logger.error(TAG, "error getting channel ticker: ${it.message}")
        }
    }

    private fun buildChannelTickerUrl(tickerUrl: String, channel: Int, type: String): String {
        val user = sessionDatasource.loginData?.loginUser.orEmpty()
        val op = sessionDatasource.operator.orEmpty()

        return "$tickerUrl/ticker?user=$user&op=$op" +
                "&channel=$channel" +
                "&device=${localDatasource.getDeviceSerial()}" +
                "&type=$type" +
                "&dt=${localDatasource.getDeviceType()}"
    }

    override suspend fun getTickerQoSData(contentId: Int): Result<QosData> {
        return runCatching {
            QosData(
                playerBw = "0",
                activeTrack = "0",
                videoBw = "0",
                bufTime = "0",
                loadLatency = "0",
                playTime = "0.0",
                primaryNode = "",
                id = contentId.toString(),
                type = "ticker",
                ip = remoteDatasource.getCurrentUserIp(),
                deviceType = localDatasource.getDeviceType()
            )
        }
    }

    override suspend fun sendHeartBeat(): Result<Unit> {
        return runCatching {
            remoteDatasource.sendHeartBeat()
        }.onFailure {
            logger.error(TAG, "sendHeartBeat error sending heartbeat $it.message")
        }
    }

    override suspend fun sendQosData(qosData: QosData): Result<Unit> {
        return runCatching {
            val completeQosDto = qosData.toDto()
                .copy(deviceType = localDatasource.getDeviceType())
                .copy(ip = remoteDatasource.getCurrentUserIp())
            logger.debug(TAG, "sendQoSData qosDataDto: $completeQosDto")
            remoteDatasource.sendQosData(completeQosDto)
        }.onFailure {
            logger.error(TAG, "sendQoSData error sending qos data $it.message")
        }
    }

    override suspend fun setBookmark(content: ContentToPlayUI, time: Long) {
        remoteDatasource.saveBookmark(
            contentId = content.identifier.getIdValue(),
            type = content.getBookmarkTypeString(),
            time = time
        )
    }

    override fun saveContentProgress(contentId: String, progress: Long) {
        logger.debug(TAG, "saveContentProgress - $contentId, $progress")
        localDatasource.setContentPlayProgress(contentId = contentId, progress = progress)
    }

    override fun getContentProgress(contentId: String) : Long {
        logger.debug(TAG, "getContentProgress - $contentId")
        return localDatasource.getContentPlayProgress(contentId = contentId)
    }

    override fun getContentProgressFlow(): Flow<Map<String, Long>> {
        return localDatasource.getContentProgressFlow()
    }

    override fun clearContentProgress() {
        localDatasource.clearContentPlayProgress()
    }
}