package com.mamm.mammapps.domain.usecases.player

import android.content.Context
import androidx.core.net.toUri
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.interfaces.PlaybackRepository
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.util.isAndroidTV
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.runBlocking

class GetVastAdUrlUseCase @Inject constructor(
    private val playbackRepository: PlaybackRepository,
    private val logger: Logger
) {

    companion object {
        private const val TAG = "GetVastAdUrlUseCase"
        private const val VAST_URL = "https://tickersapp.masmediatv.es/"
        private const val VAST_PATH = "vast"
        private const val TYPE_LIVE = "live"
        private const val TYPE_CATCHUP = "catchup"
        private const val TYPE_VOD = "vod"
        private const val DEVICE_TYPE_TV = "androidtv"
        private const val DEVICE_TYPE_WEB = "webtv"

        private const val PARAM_OP = "op"
        private const val PARAM_TYPE = "type"
        private const val PARAM_USER = "user"
        private const val PARAM_DEVICE = "device"
        private const val PARAM_DT = "dt"
        private const val PARAM_CB = "cb"
        private const val PARAM_CHANNEL = "channel"
        private const val PARAM_VOD_ID = "vod_id"
    }

    operator fun invoke(content: ContentToPlayUI): String {

        return runCatching {
            // Get parameters from repository
            val params = runBlocking {
                playbackRepository.getVastAdParameters().getOrThrow()
            }

            val baseAdUrl = "$VAST_URL$VAST_PATH"

            val type = when (content.identifier) {
                is ContentIdentifier.Channel -> TYPE_LIVE
                is ContentIdentifier.Event -> TYPE_CATCHUP
                is ContentIdentifier.VoD -> TYPE_VOD
                is ContentIdentifier.Serie -> TYPE_VOD
            }

            val dt = DEVICE_TYPE_TV
            val cb = System.currentTimeMillis() / 1000

            val builder = baseAdUrl.toUri().buildUpon()
                .appendQueryParameter(PARAM_OP, params.operator)
                .appendQueryParameter(PARAM_TYPE, type)
                .appendQueryParameter(PARAM_USER, params.userId.toString())
                .appendQueryParameter(PARAM_DEVICE, params.device)
                .appendQueryParameter(PARAM_DT, dt)
                .appendQueryParameter(PARAM_CB, cb.toString())

            when (content.identifier) {
                is ContentIdentifier.Channel -> {
                    builder.appendQueryParameter(PARAM_CHANNEL, content.identifier.id.toString())
                }
                is ContentIdentifier.Event -> {
                    val channelId = content.epgEventInfo?.fatherChannelId ?: content.identifier.id
                    builder.appendQueryParameter(PARAM_CHANNEL, channelId.toString())
                }
                is ContentIdentifier.VoD -> {
                    builder.appendQueryParameter(PARAM_VOD_ID, content.identifier.id.toString())
                }
                is ContentIdentifier.Serie -> {
                    builder.appendQueryParameter(PARAM_VOD_ID, content.identifier.id.toString())
                }
            }

            val finalUrl = builder.build().toString()
            logger.debug(TAG, "invoke - Built VAST URL: $finalUrl")
            finalUrl
        }.getOrElse { exception ->
            logger.error(TAG, "invoke - Failed to build VAST URL: ${exception.message}")
            ""
        }
    }


}
