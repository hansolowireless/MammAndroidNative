package com.mamm.mammapps.ui.viewmodel

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.openstream_flutter_rw.data.model.customdatasourcefactory.TokenParamDataSourceFactory
import com.example.openstream_flutter_rw.ui.manager.watermark.FingerprintController
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.analytics.PlaybackStatsListener
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.mamm.mammapps.R
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.Ticker
import com.mamm.mammapps.data.model.player.VideoPlayerUIState
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.player.GetDRMUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetJwTokenUseCase
import com.mamm.mammapps.domain.usecases.player.GetPlayableUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetTSTVUrlUseCase
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.extension.toDate
import com.mamm.mammapps.ui.manager.videoresize.VideoResizeManagerWithTicker
import com.mamm.mammapps.ui.mapper.toLiveEventInfoUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text
import java.time.Duration
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val tokenParamDataSourceFactory: TokenParamDataSourceFactory,
    // UseCases para reemplazar Flutter calls
    private val getPlayableUrlUseCase: GetPlayableUrlUseCase,
    private val getDRMUrlUseCase: GetDRMUrlUseCase,
    private val getTSTVUrlUseCase: GetTSTVUrlUseCase,
    private val getJwTokenUseCase: GetJwTokenUseCase,
//    private val channelZapUseCase: ChannelZapUseCase,
//    private val setNewContentUseCase: SetNewContentUseCase,
//    private val validatePINUseCase: ValidatePINUseCase,
//    private val sendQoSUseCase: SendQoSParametersUseCase,
//    private val sendBookmarkUseCase: SendBookmarkStampsUseCase,
//    private val sendHeartbeatUseCase: SendHeartbeatUseCase,
//    private val manageTickerUseCase: ManageTickerUseCase,
    private val getLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    @ApplicationContext private val context: Context,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val _uiState = MutableStateFlow(VideoPlayerUIState())
    val uiState = _uiState.asStateFlow()

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player = _player.asStateFlow()

    private val _content = MutableStateFlow<ContentToPlayUI>(getInitialContent())
    val content = _content.asStateFlow()

    //Información del evento en directo cuando se está reproduciendo un canal
    private val _liveEventInfo = MutableStateFlow<LiveEventInfoUI?>(null)
    val liveEventInfo = _liveEventInfo.asStateFlow()

    private val _tickerList = MutableStateFlow<List<Ticker>?>(null)
    val tickerList = _tickerList.asStateFlow()

    // ExoPlayer y componentes
    private var trackSelector: DefaultTrackSelector? = null
    private val statsListener: PlaybackStatsListener by lazy { PlaybackStatsListener(false) { _, _ -> } }

    // Controllers
    private var watermarkController = FingerprintController()
    private var resizeManager: VideoResizeManagerWithTicker? = null

    // Jobs para handlers periódicos
    private var qosJob: Job? = null
    private var bookmarkJob: Job? = null
    private var heartbeatJob: Job? = null
    private var channelInputJob: Job? = null

    //Either the channel URL or the VOD/Catchup Event URL
    private var playableUrl: String = ""
    private var playableLicenseUrl: String = ""

    //TSTV initial play position
    private var tstvInitialPlayPositionMs = 0L
    private val _isTstvMode = MutableStateFlow<Boolean>(false)
    val isTstvMode = _isTstvMode.asStateFlow()

    /**
     * Inicializar el player con contenido específico
     */
    fun initializeWithContent(content: ContentToPlayUI) {
        _content.update { content }
        viewModelScope.launch {
            createPlayer()

            runCatching {
                // Ejecutar ambas operaciones en paralelo
                val playableUrlDeferred = async {
                    getPlayableUrlUseCase(content)
                }
                val drmUrlDeferred = async {
                    getDRMUrlUseCase(content = content)
                }

                // Esperar ambos resultados
                val playableUrlResult = playableUrlDeferred.await()
                val drmUrlResult = drmUrlDeferred.await()

                // Verificar que ambos sean exitosos
                if (playableUrlResult.isSuccess && drmUrlResult.isSuccess) {
                    logger.debug(TAG, "initializeWithContent getPlayableUrlUseCase success")
                    playableUrl = playableUrlResult.getOrElse { "" }
                    playableLicenseUrl = drmUrlResult.getOrElse { "" }

                    setPlayerUrls(
                        videoUrl = playableUrl,
                        drmUrl = playableLicenseUrl
                    )

                } else {
                    val error =
                        playableUrlResult.exceptionOrNull() ?: drmUrlResult.exceptionOrNull()
                    logger.error(
                        TAG,
                        "initializeWithContent getPlayableUrlUseCase error = ${error?.message}"
                    )

                    _uiState.value = _uiState.value.copy(error = error?.message)
                }
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(error = exception.message)
            }
        }
    }

    private fun releasePlayer() {
        _player.value?.removeAnalyticsListener(statsListener)
        _player.value?.release()
        _player.value = null
    }

    private fun createPlayer() {

        releasePlayer()

        val builder = DefaultTrackSelector(context)
        trackSelector = builder

        _player.update { oldPlayer ->
            oldPlayer?.release()
            ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector!!)
                .build()
        }

        _player.value?.addAnalyticsListener(statsListener)
        _player.value?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                handlePlayerError(error, context)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                _uiState.value = _uiState.value.copy(isPlaying = isPlaying)
                if (isPlaying) {
                    startPeriodicFunctions()
                } else {
                    stopPeriodicFunctions()
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            }
        })

    }

    private fun setPlayerUrls(videoUrl : String, drmUrl: String = "") {
        val player = _player.value
        val content = _content.value

        var requestHeaders = emptyMap<String, String>()

        getJwTokenUseCase(_content.value).onSuccess { token ->
            requestHeaders = hashMapOf("Authorization" to "Bearer $token")
        }

        val mediaItem = MediaItem.Builder()
            .setUri(videoUrl)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("").build())
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(drmUrl)
                    .setLicenseRequestHeaders(requestHeaders)
                    .setMultiSession(true)
                    .build()
            ).build()

        val dataSourceFactory = tokenParamDataSourceFactory

        val mediaSource = DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)

        if ((content.initialPlayPositionMs) > 0) {
            player?.seekTo(content.initialPlayPositionMs ?: 0)
        }
        else if (tstvInitialPlayPositionMs > 0) {
            player?.seekTo(tstvInitialPlayPositionMs)
            tstvInitialPlayPositionMs = 0
        }
        else {
            player?.seekTo(0)
        }

        player?.prepare()
        player?.playWhenReady = true
    }

    fun observeLiveEvents() {
        if (_content.value.isLive)
            getLiveEventInfoUseCase.observeLiveEvents((_content.value.identifier).getIdValue())
                .onEach { event ->
                    // Nuevo evento iniciado o terminado
                    logger.debug(
                        TAG,
                        "startObservingLiveEvents Event changed: ${event?.getTitle()}"
                    )
                    _liveEventInfo.value = event?.toLiveEventInfoUI()
                }
                .launchIn(viewModelScope)
        else logger.info(TAG, "startObservingLiveEvents Content is not channel")
    }

    private fun startPeriodicFunctions() {
//        startHeartbeat()
//        startQoSReporting()
//        startBookmarkReporting()
//        startTickerService()
    }

    //    private fun startHeartbeat() {
//        heartbeatJob?.cancel()
//        heartbeatJob = viewModelScope.launch {
//            sendHeartbeatUseCase(firstBeat = true)
//
//            while (true) {
//                delay(120000) // 2 minutos
//                sendHeartbeatUseCase(firstBeat = false)
//            }
//        }
//    }
//
//    private fun startQoSReporting() {
//        qosJob?.cancel()
//        qosJob = viewModelScope.launch {
//            while (true) {
//                delay(60000) // 1 minuto
//                sendQoSParameters()
//            }
//        }
//    }
//
//    private fun startBookmarkReporting() {
//        val state = _uiState.value
//
//        if (state.eventType == "vod" || state.eventType == "cutv") {
//            bookmarkJob?.cancel()
//            bookmarkJob = viewModelScope.launch {
//                delay(120000) // 2 minutos inicial
//                while (true) {
//                    sendBookmarkStamps()
//                    delay(60000) // Cada minuto después
//                }
//            }
//        }
//    }
//
//    private fun sendQoSParameters() {
//        val state = _uiState.value
//        val player = _player.value
//
//        val correctedID = if (state.eventType == "cutv") {
//            "${state.cutvChannelID}-${state.eventID}"
//        } else {
//            state.eventID.toString()
//        }
//
//        val qosData = QoSData(
//            playerBw = statsListener.playbackStats?.meanBandwidth?.toString() ?: "0",
//            activeTrack = player?.videoFormat?.height?.toString() ?: "0",
//            videoBw = player?.videoFormat?.bitrate?.toString() ?: "0",
//            bufTime = statsListener.playbackStats?.rebufferRate?.toString() ?: "0",
//            loadLatency = statsListener.playbackStats?.meanJoinTimeMs?.toString() ?: "0",
//            playTime = "0.0",
//            primaryNode = player?.currentMediaItem?.localConfiguration?.uri?.host ?: "",
//            id = correctedID,
//            type = _content.value?.identifier.toString() ?: ""
//        )
//
//        viewModelScope.launch {
//            sendQoSUseCase(qosData)
//        }
//    }
//
//    private fun sendBookmarkStamps() {
//        val state = _uiState.value
//        val player = _player.value
//
//        val bookmarkData = BookmarkData(
//            position = (player?.currentPosition?.times(0.001))?.roundToInt() ?: 0,
//            duration = player?.duration ?: 0,
//            id = state.eventID ?: 0,
//            eventType = state.eventType ?: ""
//        )
//
//        viewModelScope.launch {
//            sendBookmarkUseCase(bookmarkData)
//        }
//    }
//
//    private fun startWatermarking() {
//        val state = _uiState.value
//        watermarkController.start(
//            state.fingerprint,
//            state.fingerprintFrequency,
//            state.fingerprintDuration,
//            state.fingerprintPosition,
//            watermarkInfo = state.watermark
//        )
//    }
//
//    private fun startTickerService() {
//        viewModelScope.launch {
//            manageTickerUseCase.startTicker()
//        }
//    }
//
    private fun stopPeriodicFunctions() {
        qosJob?.cancel()
        bookmarkJob?.cancel()
        heartbeatJob?.cancel()
        channelInputJob?.cancel()

        watermarkController.stop()
        resizeManager?.release()
    }

    private fun handlePlayerError(error: PlaybackException, context: Context) {
        val message = when {
            error.message?.contains("DRM") == true && context.packageName == "goandgo.openstream.com" ->
                "Contenido no disponible. Consulte opciones de compra en www.goandgotv.com"

            else -> "Error: ${error.message ?: ""}, code: ${error.errorCode}"
        }
        _uiState.value = _uiState.value.copy(error = message)
    }

    // Método para manejar eventos de Compose
    fun handleEvent(event: String) {
        val player = _player.value
//        when (event) {
//            VideoPlayerEvent.TogglePlayPause -> togglePlayPause()
//            VideoPlayerEvent.FastForward -> handleFastForward()
//            VideoPlayerEvent.Rewind -> handleRewind()
//            VideoPlayerEvent.ToggleControls -> showControls()
//            VideoPlayerEvent.Close -> {
//                // Manejar cierre - podrías emitir un evento para cerrar la pantalla
//            }
//
//            VideoPlayerEvent.ShowSettings -> showTrackSelection()
//            VideoPlayerEvent.ShowSubtitles -> showSubtitlesDialog()
//            VideoPlayerEvent.ShowAudioTracks -> showAudioTrackDialog()
//            VideoPlayerEvent.GoToLive -> goToLive()
//            VideoPlayerEvent.GoToBeginning -> goToBeginning()
//            VideoPlayerEvent.HidePINDialog -> hidePINDialog()
//            VideoPlayerEvent.SeekFinished -> {
//                // Manejar fin de seek
//            }
//
//            is VideoPlayerEvent.ValidatePIN -> validatePIN(event.pin)
//            is VideoPlayerEvent.SeekTo -> {
//                player?.seekTo(event.position)
//                updatePlayerPosition()
//            }
//        }
    }

    private fun getInitialContent(): ContentToPlayUI {
        return ContentToPlayUI(
            identifier = ContentIdentifier.VoD(-1),
            imageUrl = "",
            title = "",
            subtitle = "",
            description = "",
            deliveryURL = ""
        )
    }

    fun releaseVariables() {
        stopPeriodicFunctions()
        releasePlayer()
    }

    fun setControlVisibility(playerView: StyledPlayerView) {
        val positionView: View = playerView.findViewById(R.id.exo_position)
        val tstvHourBeginView: TextView = playerView.findViewById(R.id.tstv_hourbegin)
        val liveLabel: View = playerView.findViewById(R.id.live_indicator)
        val goToLiveButton: View = playerView.findViewById(R.id.go_live_button)
        val previewBar = playerView.findViewById<CustomPreviewBar>(R.id.exo_progress)

        configureTimeBar(previewBar)

        if (_content.value.isTimeshift && _liveEventInfo.value != null) {
            if (_liveEventInfo.value != null) {
                positionView.visibility = View.GONE
                tstvHourBeginView.visibility = View.VISIBLE
                liveLabel.visibility = View.VISIBLE

                if (previewBar?.isTstvMode == true) {
                    liveLabel.visibility = View.GONE
                    goToLiveButton.visibility = View.VISIBLE
                }
                else {
                    liveLabel.visibility = View.VISIBLE
                    goToLiveButton.visibility = View.GONE
                }

            } else {
                positionView.visibility = View.GONE
                tstvHourBeginView.visibility = View.GONE
                liveLabel.visibility = View.VISIBLE
            }
        } else {

            goToLiveButton.visibility = View.GONE

            if (!_content.value.isLive) {
                positionView.visibility = View.VISIBLE
                tstvHourBeginView.visibility = View.GONE
                liveLabel.visibility = View.GONE
            } else {
                positionView.visibility = View.GONE
                tstvHourBeginView.visibility = View.GONE
                liveLabel.visibility = View.VISIBLE
            }
        }


        val titleLabel: TextView = playerView.findViewById(R.id.channel_or_title_label)
        val liveEventTitleLabel: TextView = playerView.findViewById(R.id.live_event_title_Label)
        val contentImageView: ImageView = playerView.findViewById(R.id.contentImageView)

        titleLabel.text = _content.value.title
        liveEventTitleLabel.text = _liveEventInfo.value?.title
        Glide.with(playerView)
            .load(_content.value.imageUrl)
            .into(contentImageView)

//        // Helper function para convertir visibility a string legible
//        fun visibilityToString(visibility: Int): String {
//            return when (visibility) {
//                View.VISIBLE -> "VISIBLE"
//                View.GONE -> "GONE"
//                View.INVISIBLE -> "INVISIBLE"
//                else -> "UNKNOWN"
//            }
//        }

//        logger.debug(
//            TAG, "manageControlVisibility - isTimeshift: ${_content.value.isTimeshift}, " +
//                    "isLive: ${_content.value.isLive}, " +
//                    "liveEventInfo: ${_liveEventInfo.value}, " +
//                    "positionView: ${visibilityToString(positionView.visibility)}, " +
//                    "tstvHourBeginView: ${visibilityToString(tstvHourBeginView.visibility)}, " +
//                    "liveLabel: ${visibilityToString(liveLabel.visibility)}"
//        )
    }

    private fun configureTimeBar(previewBar: CustomPreviewBar?) {

        previewBar?.setKeyTimeIncrement(30000)

        if (_content.value.isLive) {
            if (_liveEventInfo.value != null && _content.value.isTimeshift) {
                previewBar?.setEventHourEnd(_liveEventInfo.value?.eventEnd?.toDate())
                previewBar?.setEventHourBegin(_liveEventInfo.value?.eventStart?.toDate())
                previewBar?.setIsTimeshift(_content.value.isTimeshift)
                previewBar?.visibility = View.VISIBLE
                logger.debug(TAG, "configureTimeBar - previewBar visibility: VISIBLE")
            } else {
                previewBar?.setIsTimeshift(false)
                previewBar?.visibility = View.GONE
                logger.debug(TAG, "configureTimeBar - previewBar visibility: GONE")
            }
        } else {
            previewBar?.visibility = View.VISIBLE
            logger.debug(TAG, "configureTimeBar - previewBar visibility: VISIBLE")
        }
    }

    fun handleScrubStop(previewBar: CustomPreviewBar?) {
        if (_content.value.isLive && _content.value.isTimeshift) {
            val progress = previewBar?.progress?.toLong() ?: 0
            val timeToJump =
                _liveEventInfo.value?.eventStart?.plusSeconds(progress/1000)
            val startTSTV = Duration.between(timeToJump, getCurrentDate())
                .toMillis() > PlayerConstant.MILLISECONDS_TO_BE_LIVE && (timeToJump?.isBefore(getCurrentDate()) ?: false)

            if (startTSTV) {
                viewModelScope.launch(Dispatchers.IO) {
                    getTSTVUrlUseCase.invoke(liveEventInfo = _liveEventInfo.value).onSuccess { url ->
                        logger.debug(TAG, "handleScrubStop Success getting TSTV Url, setting it now...")

                        previewBar?.isTstvMode = true
                        _isTstvMode.update { true }
                        previewBar?.setTstvPoint(_liveEventInfo.value?.eventStart?.toDate())
                        tstvInitialPlayPositionMs = progress

                        withContext(Dispatchers.Main) {
                            setPlayerUrls(videoUrl = url)
                        }

                    }.onFailure {
                        logger.error(TAG, "handleScrubStop Failed to get TSTV url, defaulting to Live Url")

                        previewBar?.isTstvMode = false
                        _isTstvMode.update { false }

                        withContext(Dispatchers.Main) {
                            setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)
                        }
                    }
                }
            } else {
                logger.debug(TAG, "handleScrubStop Difference is not sufficient to get into TSTV Mode! Still in live...")
                previewBar?.isTstvMode = false
                _isTstvMode.update { false }
                setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)
            }
        } else {
            logger.debug(TAG, "handleScrubStop No action needed after scrub stop")
        }
    }
}
