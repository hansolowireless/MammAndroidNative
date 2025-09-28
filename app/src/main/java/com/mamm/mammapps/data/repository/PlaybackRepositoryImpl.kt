package com.mamm.mammapps.data.repository

import com.mamm.mammapps.data.datasource.local.LocalDataSource
import com.mamm.mammapps.data.datasource.remote.RemoteDatasource
import com.mamm.mammapps.data.di.DeviceSerialQualifier
import com.mamm.mammapps.data.di.DeviceTypeQualifier
import com.mamm.mammapps.data.di.DrmIVQualifier
import com.mamm.mammapps.data.di.DrmSecretKeyQualifier
import com.mamm.mammapps.data.di.DrmUrlQualifier
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.session.SessionManager
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.util.Locale
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import android.util.Base64
import com.mamm.mammapps.data.config.Config
import com.mamm.mammapps.data.model.player.GetTickersResponse
import java.time.format.DateTimeFormatter

class PlaybackRepositoryImpl @Inject constructor (
    private val remoteDatasource: RemoteDatasource,
    private val localDatasource: LocalDataSource,
    @DeviceSerialQualifier private val deviceSerial: String,
    @DeviceTypeQualifier private val deviceType: String,
    @DrmUrlQualifier private val drmUrl: String,
    @DrmIVQualifier private val iV64: ByteArray,
    @DrmSecretKeyQualifier private val secretKey64: ByteArray,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : PlaybackRepository {

    companion object {
        private const val TAG = "PlaybackRepositoryImpl"
    }

    override suspend fun getVideoUrlFromCLM(deliveryURL: String, typeOfContentString: String): Result<String> {
        return runCatching {
            val locationUrl = remoteDatasource.getUrlFromCLM(deliveryURL, typeOfContentString)
            logger.debug(TAG, "getVideoUrlFromCLM - $locationUrl")
            locationUrl ?: throw Exception("Location header not found in response")
        }
    }

    override suspend fun getDRMUrl(content: ContentToPlayUI): Result<Pair<String, String>> {
        return runCatching {
            val userName = localDatasource.getUserCredentials().first
            val token = sessionManager.loginData?.token
            val userID = sessionManager.loginData?.userId
            val operatorName = Config.operatorNameDRM

            // Current date
            val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val nowString = dateFormat.format(ZonedDateTime.now())

            val signature = "$userName|$deviceType|$deviceSerial|$nowString"
            logger.debug(TAG, signature)

            // HMAC SHA-1
            val key = token?.toByteArray(Charsets.UTF_8)
            val signatureBytes = signature.toByteArray(Charsets.UTF_8)

            val mac = Mac.getInstance("HmacSHA1")
            val secretKeySpecSignature = SecretKeySpec(key, "HmacSHA1")
            mac.init(secretKeySpecSignature)
            val digest = mac.doFinal(signatureBytes)
            val digestString = digest.joinToString("") { "%02x".format(it) }

            // AuthString JSON
            val authString = """{
                "deviceID":"$deviceSerial",
                "signature":"$digestString",
                "expire":"$nowString",
                "contentType":"${content.getDRMString()}",
                "deviceType":"$deviceType"
                }""".trimMargin()
            logger.debug(TAG, authString)

            // Base64 encoding
            val encoded = Base64.encodeToString(authString.toByteArray(Charsets.UTF_8), Base64.NO_WRAP)

            // AES Encryption (CBC mode with PKCS7 padding)
            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            val secretKeySpec = SecretKeySpec(secretKey64, "AES")
            val ivSpec = IvParameterSpec(iV64)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivSpec)


            val encrypted = cipher.doFinal(encoded.toByteArray(Charsets.UTF_8))
            val encryptedString = URLEncoder.encode(
                Base64.encodeToString(encrypted, Base64.NO_WRAP),
                "UTF-8"
            )

            // Stream ID logic - basado en ContentIdentifier
            val streamID = content.identifier.getIdValue()

            // Custom data JSON
            val customData = """{
                "UserID":"$userID",
                "StreamName":"$streamID",
                "Operator":"$operatorName",
                "AuthenticationString":"$encryptedString"
                }""".trimMargin()
            logger.debug(TAG, customData)

            val customDataEncoded = Base64.encodeToString(
                customData.toByteArray(Charsets.UTF_8),
                Base64.NO_WRAP
            )

            // License URL construction (always widevine for Android)
            val licenseURL = "${drmUrl}widevine/getLicense?" +
                    "userID=$userID&" +
                    "authenticationString=$encryptedString&" +
                    "streamName=$streamID&" +
                    "operator=$operatorName"

            logger.debug(TAG, "getDRMUrl - $licenseURL")

            Pair(licenseURL, customDataEncoded)
        }.onFailure { exception ->
            logger.error(TAG, "error getting DRM URL $exception.message")

        }
    }

    override suspend fun getTickers() : Result<GetTickersResponse> {
        return runCatching {
            remoteDatasource.getTickers()
        }.onFailure {
            logger.error(TAG, "error getting tickers $it.message")
        }
    }


    override suspend fun sendHeartBeat() : Result<Unit> {
        return runCatching {
            remoteDatasource.sendHeartBeat()
        }.onFailure {
            logger.error(TAG, "sendHeartBeat error sending heartbeat $it.message")
        }
    }
}