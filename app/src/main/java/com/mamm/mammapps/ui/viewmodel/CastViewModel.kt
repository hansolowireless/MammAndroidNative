package com.mamm.mammapps.ui.viewmodel

import android.app.Application
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadRequestData
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.domain.usecases.player.GetDRMUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetJwTokenUseCase
import com.mamm.mammapps.domain.usecases.player.GetPlayableUrlUseCase
import com.mamm.mammapps.ui.extension.toMediaMetadata
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.uistate.CastState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    application: Application,
    private val getPlayableUrlUseCase: GetPlayableUrlUseCase,
    private val getDRMUrlUseCase: GetDRMUrlUseCase,
    private val getJwTokenUseCase: GetJwTokenUseCase,
    private val logger: Logger
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "CastViewModel"
    }

    private val _castState = MutableStateFlow<CastState>(CastState.NoSession)
    val castState = _castState.asStateFlow()

    private var castContext: CastContext? = null

    private val sessionManagerListener = object : SessionManagerListener<CastSession> {
        override fun onSessionStarted(session: CastSession, sessionId: String) {
            _castState.value = CastState.SessionStarted(session)
            logger.debug(TAG, "Cast session started. Session ID: $sessionId")
        }

        override fun onSessionResumed(session: CastSession, wasSuspended: Boolean) {
            _castState.value = CastState.SessionStarted(session)
            logger.debug(TAG, "Cast session resumed. Was suspended: $wasSuspended")
        }

        override fun onSessionEnded(session: CastSession, error: Int) {
            _castState.value = CastState.SessionEnded(error)
            logger.debug(TAG, "Cast session ended. Error code: $error")
        }

        override fun onSessionStarting(p0: CastSession) {
            logger.debug(TAG, "Cast session starting...")
        }

        override fun onSessionStartFailed(p0: CastSession, error: Int) {
            logger.error(TAG, "Cast session start failed. Error: $error")
        }

        override fun onSessionEnding(p0: CastSession) {
            logger.debug(TAG, "Cast session ending...")
        }

        override fun onSessionResuming(p0: CastSession, p1: String) {
            logger.debug(TAG, "Cast session resuming...")
        }

        override fun onSessionResumeFailed(p0: CastSession, error: Int) {
            logger.error(TAG, "Cast session resume failed. Error: $error")
        }

        override fun onSessionSuspended(p0: CastSession, p1: Int) {
            logger.debug(TAG, "Cast session suspended.")
        }
    }

    fun startChromecast() {
        castContext = CastContext.getSharedInstance(getApplication())
        castContext?.sessionManager?.addSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )

        logger.debug(TAG, "CastViewModel initialized and listener registered.")

        castContext?.sessionManager?.currentCastSession?.let {
            logger.debug(TAG, "Found active Cast session.")
            _castState.value = CastState.SessionStarted(it)
        } ?: run {
            _castState.value = CastState.NoSession
        }
    }

    /**
     * Carga y reproduce contenido en un dispositivo Cast.
     */
    fun loadRemoteMedia(
        content: ContentToPlayUI?
    ) {
        if (content == null) {
            logger.error(TAG, "Cannot load media on Cast, content is null.")
            return
        }
        viewModelScope.launch {
            val currentState = _castState.value
            if (currentState !is CastState.SessionStarted) {
                logger.error(TAG, "Cast attempt failed: No active Cast session.")
                return@launch
            }

            logger.debug(TAG, "Attempting to load media on Cast. Content ID: ${content.identifier.getIdValue()}")

            runCatching {
                val playableUrlDeferred =
                    async { getPlayableUrlUseCase(content, chromecast = true).getOrThrow() }

                val playableUrl = playableUrlDeferred.await()

                // Usamos Pair para almacenar la URL y el customData juntos.
                val drmData: Pair<String?, String?>

                val drmUrlFromContent = content.drmUrl?.takeIf { it.isNotBlank() }
                if (drmUrlFromContent != null) {
                    // Caso 1: La URL viene del content. Le añadimos el token. CustomData es null.
                    logger.debug(TAG, "Using DRM URL from content. Fetching token.")
                    val jwToken = getJwTokenUseCase(content, chromecast = true).getOrThrow()
                    val finalDrmUrl = drmUrlFromContent.toUri()
                        .buildUpon()
                        .appendQueryParameter("streamvxToken", jwToken)
                        .build()
                        .toString()
                    drmData = Pair(finalDrmUrl, null)
                } else {
                    // Caso 2: La URL no viene del content. Usamos el use case.
                    logger.debug(TAG, "DRM URL from content is null or blank. Fetching from UseCase.")
                    val drmInfo = getDRMUrlUseCase(
                        content
                    ).getOrNull()
                    drmData = Pair(drmInfo?.drmUrl, drmInfo?.customData)
                }

                logger.debug(TAG, "Successfully fetched URLs for Cast. PlayableURL ready, Final DRM URL: ${drmData.first}")

                buildAndLoadMedia(
                    session = currentState.session,
                    content = content,
                    playableUrl = playableUrl,
                    drmUrl = drmData.first,
                    customData = drmData.second,
                    isLive = content.isLive
                )
            }.onFailure { error ->
                logger.error(TAG, "Failed to fetch URLs or token for Cast: ${error.message}")
            }
        }
    }


    private fun buildAndLoadMedia(
        session: CastSession,
        content: ContentToPlayUI,
        playableUrl: String,
        drmUrl: String?,
        customData: String? = null,
        isLive: Boolean
    ) {
        if (playableUrl.isBlank()) {
            logger.error(TAG, "Cannot load media on Cast, playableUrl is blank.")
            return
        }

        val mediaInfoBuilder = MediaInfo
            .Builder(playableUrl)
            .setStreamType(if (isLive) MediaInfo.STREAM_TYPE_LIVE else MediaInfo.STREAM_TYPE_BUFFERED)
            .setContentType("application/dash+xml")
            .setMetadata(content.toMediaMetadata())

        if (!drmUrl.isNullOrBlank()) {
            val drmJson = JSONObject().apply {
                put("license", drmUrl)
                if (!customData.isNullOrBlank()) {
                    put("custom", customData)
                }
            }

            mediaInfoBuilder.setCustomData(drmJson)

            logger.debug(TAG, "DRM data added to Cast request with custom namespace. Data: ${drmJson.toString(2)}")
        } else {
            logger.debug(TAG, "No DRM license URL available for Cast request.")
        }

        val mediaInfo = mediaInfoBuilder.build()
        val request = MediaLoadRequestData.Builder()
            .setMediaInfo(mediaInfo)
            .setAutoplay(true)
            .build()

        logger.debug(TAG, "Sending load request to the Cast device.")
        session.remoteMediaClient?.load(request)?.setResultCallback {
            if (it.status.isSuccess) {
                logger.info(TAG, "Media loaded successfully on Cast device.")
            } else {
                logger.error(
                    TAG,
                    "Failed to load media on Cast. Status: ${it.status.statusCode} - ${it.status.statusMessage}"
                )
            }
        }
    }

    override fun onCleared() {
        castContext?.sessionManager?.removeSessionManagerListener(
            sessionManagerListener,
            CastSession::class.java
        )
        logger.debug(TAG, "CastViewModel cleared and listener removed.")
        super.onCleared()
    }
}
