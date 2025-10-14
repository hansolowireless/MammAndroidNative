package com.mamm.mammapps.ui.viewmodel

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bumptech.glide.Glide
import com.example.openstream_flutter_rw.data.model.customdatasourcefactory.TokenParamDataSourceFactory
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
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import com.mamm.mammapps.R
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.player.QosData
import com.mamm.mammapps.data.model.player.Ticker
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.GetChannelsUseCase
import com.mamm.mammapps.domain.usecases.player.GetDRMUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetJwTokenUseCase
import com.mamm.mammapps.domain.usecases.player.GetPlayableUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetTSTVUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetTickersUseCase
import com.mamm.mammapps.domain.usecases.player.SendBookmarkUseCase
import com.mamm.mammapps.domain.usecases.player.SendHeartBeatUseCase
import com.mamm.mammapps.domain.usecases.player.SendQosUseCase
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.component.player.dialogs.TrackSelectionDialog
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.extension.setHourText
import com.mamm.mammapps.ui.extension.toDate
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.mapper.toContentListUI
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.mapper.toLiveEventInfoUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.ContentListUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.model.player.ZappingInfoUI
import com.mamm.mammapps.ui.model.uistate.PlayerUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val tokenParamDataSourceFactory: TokenParamDataSourceFactory,
    // UseCases para reemplazar Flutter calls
    private val getPlayableUrlUseCase: GetPlayableUrlUseCase,
    private val getDRMUrlUseCase: GetDRMUrlUseCase,
    private val getTSTVUrlUseCase: GetTSTVUrlUseCase,
    private val getJwTokenUseCase: GetJwTokenUseCase,
    private val sendQoSUseCase: SendQosUseCase,
    private val sendBookmarkUseCase: SendBookmarkUseCase,
    private val sendHeartbeatUseCase: SendHeartBeatUseCase,
    private val getLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getTickersUseCase: GetTickersUseCase,
    @ApplicationContext private val context: Context,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val _playerState = MutableStateFlow<PlayerUIState>(PlayerUIState.Idle)
    val playerState = _playerState.asStateFlow()

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player = _player.asStateFlow()

    private val _content = MutableStateFlow<ContentToPlayUI>(getInitialContent())
    val content = _content.asStateFlow()

    //Mostrar el layer de zapping
    private val _showZappingLayer = MutableStateFlow<Boolean>(false)
    val showZappingLayer = _showZappingLayer.asStateFlow()

    //Lista de canales para zapping
    private val _zappingInfo = MutableStateFlow<List<ZappingInfoUI>>(emptyList())
    val zappingInfo = _zappingInfo.asStateFlow()

    //Información del evento en directo cuando se está reproduciendo un canal
    private val _liveEventInfo = MutableStateFlow<LiveEventInfoUI?>(null)
    val liveEventInfo = _liveEventInfo.asStateFlow()

    //Tickers
    private val _tickerList = MutableStateFlow<List<Ticker>>(emptyList())
    val tickerList = _tickerList.asStateFlow()

    // ExoPlayer y componentes
    private var trackSelector: DefaultTrackSelector? = null
    private val statsListener: PlaybackStatsListener by lazy { PlaybackStatsListener(false) { _, _ -> } }

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
                    //TODO SHOW ERROR
                }
            }.onFailure { exception ->
                //TODO SHOW ERROR
            }
        }
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
                if (isPlaying) {
                    startPeriodicFunctions()
                    _playerState.update { PlayerUIState.Playing }
                } else {
                    stopPeriodicFunctions()
                    _playerState.update { PlayerUIState.Paused }
                }
            }

            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {

            }
        })

    }

    private fun setPlayerUrls(videoUrl: String, drmUrl: String = "") {
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
        } else if (tstvInitialPlayPositionMs > 0) {
            player?.seekTo(tstvInitialPlayPositionMs)
            tstvInitialPlayPositionMs = 0
        } else {
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

    fun observeTickers() {
        getTickersUseCase.observeTickers().onEach { tickers ->
            _tickerList.value = tickers
        }.launchIn(viewModelScope)
    }

    private fun startPeriodicFunctions() {
        startHeartbeat()
        startQoSReporting()
        startBookmarkReporting()
    }

    private fun startHeartbeat() {
        heartbeatJob?.cancel()
        heartbeatJob = viewModelScope.launch(Dispatchers.IO) {
            logger.debug(TAG, "startHeartbeat Starting to send heartbeat...")
            sendHeartbeatUseCase()
            while (true) {
                delay(120000)

                logger.debug(TAG, "startHeartbeat Sending another heartbeat...")
                sendHeartbeatUseCase()
            }
        }
    }

    private fun startQoSReporting() {
        qosJob?.cancel()
        qosJob = viewModelScope.launch {
            while (true) {
                delay(60000)
                logger.debug(TAG, "startQoSReporting Sending qos data...")
                val qosData = createQosData()
                withContext(Dispatchers.IO) {
                    sendQoSUseCase(qosData)
                }
            }
        }
    }

    private fun createQosData(): QosData {
        val player = _player.value
        return QosData(
            playerBw = statsListener.playbackStats?.meanBandwidth?.toString() ?: "0",
            activeTrack = player?.videoFormat?.height?.toString() ?: "0",
            videoBw = player?.videoFormat?.bitrate?.toString() ?: "0",
            bufTime = statsListener.playbackStats?.rebufferRate?.toString() ?: "0",
            loadLatency = statsListener.playbackStats?.meanJoinTimeMs?.toString() ?: "0",
            playTime = "0.0",
            primaryNode = player?.currentMediaItem?.localConfiguration?.uri?.host ?: "",
            id = _content.value.identifier.id.toString(),
            type = _content.value.identifier.toString()
        )
    }

    private fun startBookmarkReporting() {
        if (_content.value.identifier is ContentIdentifier.VoD || _content.value.identifier is ContentIdentifier.Event) {
            bookmarkJob?.cancel()
            bookmarkJob = viewModelScope.launch {
                logger.debug(TAG, "startBookmarkReporting in 2 minutes...")
                delay(120000) // 2 minutos inicial
                while (true) {
                    sendBookmarkStamps()
                    delay(60000)
                }
            }
        } else {
            logger.info(
                TAG,
                "startBookmarkReporting Content is not VOD or Event, won't start bookmark report"
            )
        }
    }

    private fun sendBookmarkStamps() {
        val player = _player.value
        viewModelScope.launch {
            sendBookmarkUseCase(
                content = _content.value,
                time = player?.currentPosition ?: 0
            )
        }
    }

    private fun handlePlayerError(exception: PlaybackException, context: Context) {
        //TODO SHOW ERROR
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

    fun setControlVisibility(playerView: StyledPlayerView) {
        val positionView: View = playerView.findViewById(R.id.exo_position)
        val tstvHourBeginView: TextView = playerView.findViewById(R.id.tstv_hourbegin)
        val liveLabel: View = playerView.findViewById(R.id.live_indicator)

        val goToLiveButton: AppCompatImageButton = playerView.findViewById(R.id.go_live_button)
        val startOverButton: View = playerView.findViewById(R.id.go_beginning_button)

        playerView.setShowNextButton(false)
        playerView.setShowPreviousButton(false)

        val previewBar = playerView.findViewById<CustomPreviewBar>(R.id.exo_progress)

        configureTimeBar(previewBar)

        if (_content.value.isTimeshift) {
            if (_liveEventInfo.value != null) {
                tstvHourBeginView.visibility = View.VISIBLE
                positionView.visibility = View.GONE
                startOverButton.visibility = View.VISIBLE
                tstvHourBeginView.setHourText(_liveEventInfo.value?.eventStart)

                if (previewBar?.isTstvMode == true) {
                    liveLabel.visibility = View.GONE
                    goToLiveButton.visibility = View.VISIBLE
                } else {
                    liveLabel.visibility = View.VISIBLE
                    goToLiveButton.visibility = View.GONE
                }

            } else {
                positionView.visibility = View.GONE
                tstvHourBeginView.visibility = View.GONE
                liveLabel.visibility = View.VISIBLE
                startOverButton.visibility = View.GONE
            }
        } else {
            goToLiveButton.visibility = View.GONE
            startOverButton.visibility = View.GONE

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

    }

    fun setDialogButtonVisibility(
        ccTracksButton: AppCompatImageButton?,
        audioTracksButton: AppCompatImageButton?
    ) {
        runCatching {
            if (TrackSelectionDialog.willHaveCCContent(_player.value)) {
                ccTracksButton?.visibility = View.VISIBLE
            } else {
                ccTracksButton?.visibility = View.GONE
            }

            if (TrackSelectionDialog.willHaveAudioContent(_player.value)) {
                audioTracksButton?.visibility = View.VISIBLE
            } else {
                audioTracksButton?.visibility = View.GONE
            }
        }.onFailure {
            logger.error(
                TAG,
                "setDialogButtonVisibility - Error setting button visibility: ${it.message}"
            )
        }
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

    fun triggerTSTVMode(previewBar: CustomPreviewBar?, forcePosition: Long? = null) {
        if (_content.value.isLive && _content.value.isTimeshift) {
            val progress = forcePosition ?: previewBar?.progress?.toLong() ?: 0
            val timeToJump =
                _liveEventInfo.value?.eventStart?.plusSeconds(progress / 1000)
            val startTSTV = Duration.between(timeToJump, getCurrentDate())
                .toMillis() > PlayerConstant.MILLISECONDS_TO_BE_LIVE && (timeToJump?.isBefore(
                getCurrentDate()
            ) ?: false)

            if (startTSTV) {
                viewModelScope.launch(Dispatchers.IO) {
                    getTSTVUrlUseCase.invoke(liveEventInfo = _liveEventInfo.value)
                        .onSuccess { url ->
                            logger.debug(
                                TAG,
                                "handleScrubStop Success getting TSTV Url, setting it now..."
                            )

                            previewBar?.isTstvMode = true
                            _isTstvMode.update { true }
                            previewBar?.setTstvPoint(_liveEventInfo.value?.eventStart?.toDate())
                            tstvInitialPlayPositionMs = progress

                            withContext(Dispatchers.Main) {
                                setPlayerUrls(videoUrl = url)
                            }

                        }.onFailure {
                            logger.error(
                                TAG,
                                "handleScrubStop Failed to get TSTV url, defaulting to Live Url"
                            )

                            previewBar?.isTstvMode = false
                            _isTstvMode.update { false }

                            withContext(Dispatchers.Main) {
                                setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)
                            }
                        }
                }
            } else {
                logger.debug(
                    TAG,
                    "handleScrubStop Difference is not sufficient to get into TSTV Mode! Still in live..."
                )
                previewBar?.isTstvMode = false
                _isTstvMode.update { false }
                setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)
            }
        } else {
            logger.debug(TAG, "handleScrubStop No action needed after scrub stop")
        }
    }

    fun releaseVariables() {
        stopPeriodicFunctions()
        releasePlayer()
    }

    private fun stopPeriodicFunctions() {
        qosJob?.cancel()
        bookmarkJob?.cancel()
        heartbeatJob?.cancel()
        channelInputJob?.cancel()
    }

    private fun releasePlayer() {
        _player.value?.removeAnalyticsListener(statsListener)
        _player.value?.release()
        _player.value = null
    }

    fun showZappingLayer() {
        _showZappingLayer.update { true }
    }

    fun hideZappingLayer() {
        _showZappingLayer.update { false }
    }

    fun updateChannelList() {
        if (_content.value.identifier is ContentIdentifier.Channel) {
            viewModelScope.launch(Dispatchers.IO) {
                getChannelsUseCase().onSuccess { channels ->
                    _zappingInfo.update {
                        channels.map { channel ->
                            ZappingInfoUI(
                                channel = channel.toContentEntityUI(),
                                liveEvent = getLiveEventInfoUseCase(channelId = channel.id)?.toContentListUI() ?:
                                ContentListUI(identifier = ContentIdentifier.Event(0), title = channel.name.orEmpty(), imageUrl = "")
                            )
                        }
                    }
                }.onFailure {
                    logger.error(TAG, "updateChannelList Error getting channels for Zapping List")
                }
            }
        }
    }

    fun findAndPlayChannel(content: ContentEntityUI) {
        viewModelScope.launch (Dispatchers.IO) {
            getChannelsUseCase().onSuccess { channels ->
                val channel = channels.find { it.id == content.identifier.id }
                channel?.let {
                    initializeWithContent(it.toContentToPlayUI())
                } ?: logger.error(TAG, "findAndPlayChannel - Channel not found")
            }
        }
    }

}
