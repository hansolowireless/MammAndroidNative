package com.mamm.mammapps.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultLoadControl
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.drm.DefaultDrmSessionManager
import com.google.android.exoplayer2.drm.DefaultDrmSessionManagerProvider
import com.google.android.exoplayer2.drm.DrmSessionManagerProvider
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.customdatasourcefactory.DynamicHttpMediaDrmCallback
import com.mamm.mammapps.data.model.player.customdatasourcefactory.TokenParamDataSourceFactory
import com.mamm.mammapps.domain.model.exception.SessionException
import com.mamm.mammapps.domain.model.player.TickerInfo
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.logout.LogoutUseCase
import com.mamm.mammapps.domain.usecases.player.GetDRMUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetJwTokenUseCase
import com.mamm.mammapps.domain.usecases.player.GetPlayableUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetTSTVUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetTickersUseCase
import com.mamm.mammapps.domain.usecases.player.playprogresscache.GetPlayProgressUseCaseSync
import com.mamm.mammapps.domain.usecases.player.playprogresscache.SavePlayProgressUseCase
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.extension.inferMimeType
import com.mamm.mammapps.ui.extension.toDate
import com.mamm.mammapps.ui.mapper.toLiveEventInfoUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.model.player.helper.BookmarkTracker
import com.mamm.mammapps.ui.model.player.helper.HeartbeatTracker
import com.mamm.mammapps.ui.model.player.helper.QosReporter
import com.mamm.mammapps.ui.model.player.helper.ZappingController
import com.mamm.mammapps.ui.model.uistate.PlayerUIState
import com.mamm.mammapps.util.getCurrentDate
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.Duration
import javax.inject.Inject

@HiltViewModel
class VideoPlayerViewModel @Inject constructor(
    private val tokenParamDataSourceFactory: TokenParamDataSourceFactory,
    private val getPlayableUrlUseCase: GetPlayableUrlUseCase,
    private val getDRMUrlUseCase: GetDRMUrlUseCase,
    private val getTSTVUrlUseCase: GetTSTVUrlUseCase,
    private val getJwTokenUseCase: GetJwTokenUseCase,
    private val getLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    private val getTickersUseCase: GetTickersUseCase,
    private val savePlayProgressUseCase: SavePlayProgressUseCase,
    private val getPlayProgressUseCase: GetPlayProgressUseCaseSync,
    private val logoutUseCase: LogoutUseCase,
    private val heartbeatTracker: HeartbeatTracker,
    private val qosReporter: QosReporter,
    private val bookmarkTracker: BookmarkTracker,
    private val zappingController: ZappingController,
    @ApplicationContext private val context: Context,
    val logger: Logger
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
    val showZappingLayer = zappingController.showZappingLayer

    //Lista de canales para zapping
    val zappingInfo = zappingController.zappingInfo

    //Información del evento en directo cuando se está reproduciendo un canal
    private val _liveEventInfo = MutableStateFlow<LiveEventInfoUI?>(null)
    val liveEventInfo = _liveEventInfo.asStateFlow()

    //Tickers
    private val _tickerInfo = MutableStateFlow<TickerInfo?>(null)
    val tickerInfo = _tickerInfo.asStateFlow()

    //Display del número del canal para hacer zapping
    val zappingNumberDisplay = zappingController.zappingNumberDisplay

    // ExoPlayer y componentes
    private var trackSelector: DefaultTrackSelector? = null

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
                val drmInfoDeferred = async {
                    getDRMUrlUseCase(content = content)
                }

                // Esperar ambos resultados
                val playableUrlResult = playableUrlDeferred.await()
                val drmInfoResult = drmInfoDeferred.await()

                // Verificar que ambos sean exitosos
                if (playableUrlResult.isSuccess && drmInfoResult.isSuccess) {
                    logger.debug(TAG, "initializeWithContent getPlayableUrlUseCase success")
                    playableUrl = playableUrlResult.getOrElse { "" }
                    playableLicenseUrl = drmInfoResult.map {it.drmUrl}.getOrElse { "" }

                    setPlayerUrls(
                        videoUrl = playableUrl,
                        drmUrl = playableLicenseUrl
                    )

                } else {
                    val error =
                        playableUrlResult.exceptionOrNull() ?: drmInfoResult.exceptionOrNull()
                    logger.error(TAG, "initializeWithContent getPlayableUrlUseCase error = ${error?.message}")
                    //TODO SHOW ERROR
                }
            }.onFailure {
                //TODO SHOW ERROR
                handleSessionExpired(it)
            }
        }
    }

    private fun createPlayer() {

        val builder = DefaultTrackSelector(context)
        trackSelector = builder

        _player.update { oldPlayer ->
            oldPlayer?.stop()
            oldPlayer?.release()

            val loadControl = DefaultLoadControl.Builder()
                .setBufferDurationsMs(
                    DefaultLoadControl.DEFAULT_MIN_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_MAX_BUFFER_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_MS,
                    DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS
                )
                .build()

            ExoPlayer.Builder(context)
                .setTrackSelector(trackSelector!!)
                .build()
        }

        _player.value?.let { qosReporter.registerPlayer(it) }
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

    private suspend fun setPlayerUrls(videoUrl: String, drmUrl: String = "") {
        withContext(Dispatchers.Main) {
            val player = _player.value ?: return@withContext
            val content = _content.value

            val mediaItem = buildMediaItem(videoUrl, drmUrl)
            val drmProvider = buildDrmSessionManagerProvider(drmUrl)
            val dataSourceFactory = tokenParamDataSourceFactory.also { it.resetTokenMode() }
            val mediaSource = buildMediaSource(mediaItem, drmProvider, dataSourceFactory)

            player.setMediaSource(mediaSource)
            seekToCorrectPosition(player, content)
            player.prepare()
            player.playWhenReady = true
        }
    }

    private fun buildMediaItem(videoUrl: String, drmUrl: String): MediaItem {
        //TODO: asignar la metadata mediante setMediaMetadata?
        val builder = MediaItem.Builder()
            .setUri(videoUrl)
            .setMimeType(videoUrl.inferMimeType())

        if (drmUrl.isNotEmpty()) {
            builder.setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(drmUrl)
                    .setMultiSession(true)
                    .build()
            )
        }
        return builder.build()
    }

    private fun buildDrmSessionManagerProvider(drmUrl: String): DrmSessionManagerProvider {
        if (drmUrl.isEmpty()) return DefaultDrmSessionManagerProvider()

        val drmCallback = DynamicHttpMediaDrmCallback(
            defaultLicenseUrl = drmUrl,
            dataSourceFactory = DefaultHttpDataSource.Factory(),
            tokenProvider = {
                runBlocking(Dispatchers.IO) {
                    getJwTokenUseCase(_content.value).getOrNull()
                }
            },
            logger = logger
        )

        val drmSessionManager = DefaultDrmSessionManager.Builder()
            .setMultiSession(true)
            .setPlayClearSamplesWithoutKeys(true)
            .setUseDrmSessionsForClearContent(C.TRACK_TYPE_VIDEO, C.TRACK_TYPE_AUDIO)
            .build(drmCallback)

        return DrmSessionManagerProvider { drmSessionManager }
    }

    private fun buildMediaSource(
        mediaItem: MediaItem,
        drmProvider: DrmSessionManagerProvider,
        dataSourceFactory: TokenParamDataSourceFactory
    ): MediaSource {
        return DefaultMediaSourceFactory(dataSourceFactory)
            .setDrmSessionManagerProvider(drmProvider)
            .createMediaSource(mediaItem)
    }

    private fun seekToCorrectPosition(player: ExoPlayer, content: ContentToPlayUI) {
        if (content.initialPlayPositionMs > 0) {
            player.seekTo(content.initialPlayPositionMs)
        } else if (tstvInitialPlayPositionMs > 0) {
            player.seekTo(tstvInitialPlayPositionMs)
            tstvInitialPlayPositionMs = 0
        } else {
            player.seekTo(getPlayProgress())
        }
    }

    fun pausePlayer() {
        logger.debug(TAG, "pausePlayer")
        _player.value?.pause()
    }

    fun playPlayer() {
        logger.debug(TAG, "playPlayer")
        _player.value?.play()
    }

    fun observeLiveEvents() {
        if (_content.value.isLive)
            getLiveEventInfoUseCase.observeLiveEvents((_content.value.identifier).getIdValue())
                .onEach { event ->
                    // Nuevo evento iniciado o terminado
                    logger.debug(TAG, "startObservingLiveEvents Event changed: ${event?.title}")
                    _liveEventInfo.value = event?.toLiveEventInfoUI()
                }
                .launchIn(viewModelScope)
        else logger.info(TAG, "startObservingLiveEvents Content is not channel")
    }

    fun observeTickers() {
        getTickersUseCase.observeTickers().onEach { tickers ->
            _tickerInfo.value = tickers
        }.launchIn(viewModelScope)
    }

    private fun startPeriodicFunctions() {
        heartbeatTracker.startHeartbeat(viewModelScope) { handleSessionExpired(it) }
        qosReporter.startReporting(viewModelScope, { _player.value }, { _content.value })
        bookmarkTracker.startTracking(viewModelScope, { _player.value }, { _content.value })
    }

    private suspend fun handleSessionExpired(exception: Throwable) {
        withContext(Dispatchers.Main) {
            if (exception is SessionException) {
                _playerState.update { PlayerUIState.Session }
                releaseVariables()
                logoutUseCase.invoke()
            }
            else {
                logger.warn(TAG, "handleSessionExpired - Error in pushSession or CLM request, but was not unauthorized: ${exception.message}")
            }
        }
    }

    fun callQoSTicker() {
        qosReporter.callQoSTicker(viewModelScope, _content.value.identifier)
    }



    private fun handlePlayerError(exception: PlaybackException, context: Context) {
        when (exception.errorCode) {
            PlaybackException.ERROR_CODE_BEHIND_LIVE_WINDOW -> {
                logger.error(TAG, "handlePlayerError - ERROR_CODE_BEHIND_LIVE_WINDOW")
                _player.value?.seekToDefaultPosition()
                _player.value?.prepare()
            }
            else -> {
                _playerState.update { PlayerUIState.Error(exception.localizedMessage.orEmpty() + ": " + exception.errorCode) }
            }
        }
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
                            logger.debug(TAG, "handleScrubStop Success getting TSTV Url, setting it now...")

                            previewBar?.isTstvMode = true
                            previewBar?.setTstvPoint(_liveEventInfo.value?.eventStart?.toDate())
                            tstvInitialPlayPositionMs = progress
                            _isTstvMode.update { true }

                            setPlayerUrls(videoUrl = url)

                        }.onFailure {
                            logger.error(TAG, "handleScrubStop Failed to get TSTV url, defaulting to Live Url")

                            previewBar?.isTstvMode = false
                            _isTstvMode.update { false }

                            setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)

                        }
                }
            } else {
                logger.debug(TAG, "handleScrubStop Difference is not sufficient to get into TSTV Mode! Still in live...")
                previewBar?.isTstvMode = false
                _isTstvMode.update { false }
                viewModelScope.launch {
                    setPlayerUrls(videoUrl = playableUrl, drmUrl = playableLicenseUrl)
                }
            }
        } else {
            logger.debug(TAG, "handleScrubStop No action needed after scrub stop")
        }
    }

    fun setLivePosition (previewBar: CustomPreviewBar?) {
        logger.debug(TAG, "setLivePosition")
        val currentLivePosition = Duration.between(_liveEventInfo.value?.eventStart, getCurrentDate())
            .toMillis()
        triggerTSTVMode(previewBar, forcePosition = currentLivePosition)
    }

    fun showZappingLayer() {
        logger.debug(TAG, "showZappingLayer")
        zappingController.showZappingLayer(_content.value)
    }

    fun hideZappingLayer() {
        logger.debug(TAG, "hideZappingLayer")
        zappingController.hideZappingLayer()
    }

    fun showZappingNumberDisplay(newDigit: String) {
        zappingController.showZappingNumberDisplay(newDigit)
    }

    fun updateChannelList() {
        logger.debug(TAG, "updateChannelList")
        zappingController.updateChannelList(_content.value, viewModelScope)
    }

    fun findAndPlayChannel(content: ContentEntityUI) {
        zappingController.findAndPlayChannel(content, viewModelScope) {
            initializeWithContent(it)
        }
    }

    fun navigateToChannel(number: String) {
        zappingController.navigateToChannel(number, viewModelScope) {
            initializeWithContent(it)
        }
    }

    fun navigateToNextChannel() {
        zappingController.navigateToNextChannel(_content.value, viewModelScope) {
            initializeWithContent(it)
        }
    }

    fun navigateToPreviousChannel() {
        zappingController.navigateToPreviousChannel(_content.value, viewModelScope) {
            initializeWithContent(it)
        }
    }

    private fun stopPeriodicFunctions() {
        logger.debug(TAG, "stopPeriodicFunctions")
        qosReporter.stopReporting()
        bookmarkTracker.stopTracking()
        heartbeatTracker.stopHeartbeat()
    }

    private fun releasePlayer() {
        logger.debug(TAG, "releasePlayer")
        _player.value?.let { qosReporter.unregisterPlayer(it) }
        _player.value?.release()
        _player.value = null
    }

    private fun savePlayProgress() {
        if (_content.value.identifier !is ContentIdentifier.Channel) {
            logger.debug(TAG, "savePlayProgress")
            savePlayProgressUseCase.invoke(
                _content.value.identifier.id.toString(),
                _player.value?.currentPosition ?: 0)
        }
    }

    private fun getPlayProgress() : Long {
        return getPlayProgressUseCase.invoke(
            _content.value.identifier.id
        ).let { progress ->
            logger.debug(TAG, "getPlayProgress, $progress")
            progress
        }
    }

    fun releaseVariables() {
        savePlayProgress()
        stopPeriodicFunctions()
        releasePlayer()
    }

}
