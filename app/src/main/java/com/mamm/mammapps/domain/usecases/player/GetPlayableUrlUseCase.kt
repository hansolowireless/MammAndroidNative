package com.mamm.mammapps.domain.usecases.player

import com.mamm.mammapps.data.extension.toTSTVDateString
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import java.net.URI
import javax.inject.Inject

class GetPlayableUrlUseCase @Inject constructor(
    private val tokenRepository: TokenRepository,
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetPlayableUrlUseCase"
    }

    suspend operator fun invoke(
        content: ContentToPlayUI,
        chromecast: Boolean = false
    ): Result<String> =
        runCatching {
            val playableURL = playbackRepository.getVideoUrlFromCLM(
                deliveryURL = content.deliveryURL,
                typeOfContentString = content.getCLMString(),
                chromecast = chromecast
            ).getOrThrow().let { url ->

                val finalPlayableURL = modifyUrlToCatchupIfNeeded(
                    content = content,
                    playableUrl = url
                )

                tokenRepository.refreshIp().onFailure { exception ->
                    throw exception
                }
                tokenRepository.storeK1KeyEncrypted(finalPlayableURL).onFailure { exception ->
                    throw exception
                }
                finalPlayableURL
            }

            playableURL
        }.onSuccess {
            Result.success(it)
        }.onFailure {
            Result.success(it)
        }

    private fun modifyUrlToCatchupIfNeeded(content: ContentToPlayUI, playableUrl: String): String {
        if (content.identifier !is ContentIdentifier.Event) {
            logger.debug(TAG, "tuneUrlToCatchupIfNeeded URL is not an event, will not be tuned")
            return playableUrl
        }

        val event = content.epgEventInfo ?: run {
            logger.debug(TAG, "tuneUrlToCatchupIfNeeded EPG event info is null, will not be tuned")
            return playableUrl
        }

        val startTime = event.eventStart ?: run {
            logger.debug(
                TAG,
                "tuneUrlToCatchupIfNeeded Event start time is null, will not be tuned"
            )
            return playableUrl
        }

        val endTime = event.eventEnd ?: run {
            logger.debug(TAG, "tuneUrlToCatchupIfNeeded Event end time is null, will not be tuned")
            return playableUrl
        }


        if (endTime.isBefore(startTime)) {
            throw IllegalArgumentException("End time cannot be before start time")
        }

        return try {
            val originalUri = URI(playableUrl)
            val baseUrl = buildCatchupBaseUrl(originalUri)
            val baseUri = URI(baseUrl)

            val timeStartParam = "timeStart=${startTime.toTSTVDateString()}"
            val timeEndParam = "timeEnd=${endTime.toTSTVDateString()}"
            val originalQuery = originalUri.query ?: ""

            val queryString = if (originalQuery.isEmpty()) {
                "$timeStartParam&$timeEndParam"
            } else {
                "$timeStartParam&$timeEndParam&$originalQuery"
            }

            val catchupURL =
                "${baseUri.scheme}://${baseUri.authority}${baseUri.path}playlist.mpd?$queryString"
            logger.debug(
                TAG,
                "tuneUrlToCatchupIfNeeded URL has been tuned to conform Catchup URL $catchupURL"
            )

            catchupURL
        } catch (e: Exception) {
            throw IllegalArgumentException("Failed to build catchup URL: ${e.message}")
        }
    }

    private fun buildCatchupBaseUrl(uri: URI): String {
        val fullUrl = uri.toString()

        return if (fullUrl.contains("smil:")) {
            val smilIndex = fullUrl.indexOf("smil:")
            fullUrl.substring(0, smilIndex)
        } else {
            fullUrl
        }
    }


}