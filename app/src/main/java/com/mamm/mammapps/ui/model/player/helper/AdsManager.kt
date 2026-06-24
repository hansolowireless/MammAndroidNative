package com.mamm.mammapps.ui.model.player.helper

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider
import com.google.android.exoplayer2.ext.ima.ImaAdsLoader
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.ui.AdViewProvider
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.customdatasourcefactory.TokenParamDataSourceFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdsManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val logger: Logger
) {
    companion object {
        private const val TAG = "AdsManager"
    }

    private var adsLoader: ImaAdsLoader? = null

    /**
     * Inicializa el ImaAdsLoader y lo vincula al player
     */
    fun setupAdsLoader(player: ExoPlayer) {
        if (adsLoader == null) {
            logger.debug(TAG, "setupAdsLoader - Creating new ImaAdsLoader")
            adsLoader = ImaAdsLoader.Builder(context)
                .setAdEventListener { adEvent ->
                    logger.debug(TAG, "IMA Ad Event: ${adEvent.type}")
                }
                .setAdErrorListener { adErrorEvent ->
                    logger.error(TAG, "IMA Ad Error: ${adErrorEvent.error.message} (Code: ${adErrorEvent.error.errorCode})")
                }
                .build()
        }
        adsLoader?.setPlayer(player)
        logger.debug(TAG, "setupAdsLoader - ImaAdsLoader linked to player")
    }

    /**
     * Crea un MediaSource con soporte para anuncios
     */
    fun createMediaSourceWithAds(
        mediaItem: MediaItem,
        drmProvider: DrmSessionManagerProvider,
        dataSourceFactory: TokenParamDataSourceFactory,
        adTagUrl: String,
        adViewProvider: AdViewProvider?
    ): MediaSource {
        val factory = DefaultMediaSourceFactory(dataSourceFactory)
            .setDrmSessionManagerProvider(drmProvider)

        val adsLoaderInstance = adsLoader
        if (adsLoaderInstance != null && adTagUrl.isNotEmpty() && adViewProvider != null) {
            logger.debug(TAG, "createMediaSourceWithAds - Setting up ads with tag: $adTagUrl")
            factory.setLocalAdInsertionComponents(
                { adsLoaderInstance },
                adViewProvider
            )
        } else {
            logger.debug(TAG, "createMediaSourceWithAds - No ads configured for this content")
        }

        return factory.createMediaSource(mediaItem)
    }

    /**
     * Limpia el adsLoader cuando se termina de usar el player
     */
    fun detachPlayer() {
        logger.debug(TAG, "detachPlayer - Releasing ads loader from player")
        adsLoader?.setPlayer(null)
    }

    /**
     * Libera completamente el adsLoader
     */
    fun release() {
        logger.debug(TAG, "release - Releasing ads loader")
        adsLoader?.release()
        adsLoader = null
    }
}
