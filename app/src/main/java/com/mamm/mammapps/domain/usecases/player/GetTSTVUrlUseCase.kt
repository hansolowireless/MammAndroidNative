package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.extension.toTSTVDateString
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import java.net.URI
import java.time.ZonedDateTime
import javax.inject.Inject

class GetTSTVUrlUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetTSTVUrlUseCase"
    }

    suspend operator fun invoke(liveEventInfo: LiveEventInfoUI?): Result<String> {
        if (liveEventInfo == null) {
            return Result.failure(Exception("GetTSTVUrlUseCase LiveEventInfoUI must not be null"))
        }

        return runCatching {
            val url = playbackRepository.getVideoUrlFromCLM(
                deliveryURL = liveEventInfo.deliveryURL,
                typeOfContentString = "VOD"
            ).getOrThrow()

            val finalUrl = modifyUrlToTSTV(
                eventStart = liveEventInfo.eventStart,
                playableUrl = url
            )

            tokenRepository.refreshIp().getOrThrow()
            tokenRepository.storeK1KeyEncrypted(finalUrl).getOrThrow()

            finalUrl
        }
    }

    private fun modifyUrlToTSTV(eventStart: ZonedDateTime?, playableUrl: String): String {

        if (eventStart == null) {
            throw Exception("modifyUrlToTSTV Event start must not be null")
        }

        return try {
            val originalUri = URI(playableUrl)
            val baseUrl = buildTSTVBaseUrl(originalUri)
            val baseUri = URI(baseUrl)

            val timeStartParam = "timeStart=${eventStart.toTSTVDateString()}"
            val originalQuery = originalUri.query ?: ""

            val queryString = if (originalQuery.isEmpty()) {
                timeStartParam
            } else {
                "$timeStartParam&$originalQuery"
            }

            val tstvUrl =
                "${baseUri.scheme}://${baseUri.authority}${baseUri.path}playlist.mpd?$queryString"
            logger.debug(TAG, "modifyUrlToTSTV URL has been tuned to conform TSTV URL $tstvUrl")

            tstvUrl
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to build tstv URL: ${e.message}")
        }
    }

    private fun buildTSTVBaseUrl(uri: URI): String {
        val fullUrl = uri.toString()

        return if (fullUrl.contains("smil:")) {
            val smilIndex = fullUrl.indexOf("smil:")
            fullUrl.substring(0, smilIndex)
        } else {
            fullUrl
        }
    }

}