package com.mamm.mammapps.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.openstream_flutter_rw.data.model.customdatasourcefactory.TokenParamDataSourceFactory
import com.mamm.mammapps.ui.manager.videoresize.VideoResizeManagerWithTicker
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
import com.google.android.exoplayer2.util.MimeTypes
import com.mamm.mammapps.data.logger.Logger
import com.mamm.mammapps.data.model.player.VideoPlayerUIState
import com.mamm.mammapps.domain.interfaces.TokenRepository
import com.mamm.mammapps.domain.usecases.FindLiveEventOnChannelUseCase
import com.mamm.mammapps.domain.usecases.player.GetDRMUrlUseCase
import com.mamm.mammapps.domain.usecases.player.GetJwTokenUseCase
import com.mamm.mammapps.domain.usecases.player.GetPlayableUrlUseCase
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val tokenParamDataSourceFactory: TokenParamDataSourceFactory,
    // UseCases para reemplazar Flutter calls
    private val getPlayableUrlUseCase: GetPlayableUrlUseCase,
    private val getDRMUrlUseCase: GetDRMUrlUseCase,
    private val getJwTokenUseCase: GetJwTokenUseCase,
//    private val channelZapUseCase: ChannelZapUseCase,
//    private val setNewContentUseCase: SetNewContentUseCase,
//    private val validatePINUseCase: ValidatePINUseCase,
//    private val sendQoSUseCase: SendQoSParametersUseCase,
//    private val sendBookmarkUseCase: SendBookmarkStampsUseCase,
//    private val sendHeartbeatUseCase: SendHeartbeatUseCase,
//    private val manageTickerUseCase: ManageTickerUseCase,
    private val getNewLiveEventInfoUseCase: FindLiveEventOnChannelUseCase,
    @ApplicationContext private val context : Context,
    private val logger: Logger
) : ViewModel() {

    companion object {
        private const val TAG = "PlayerViewModel"
    }

    private val _uiState = MutableStateFlow(VideoPlayerUIState())
    val uiState = _uiState.asStateFlow()

    private val _content = MutableStateFlow<ContentToPlayUI?>(null)
    val content = _content.asStateFlow()

    private val _player = MutableStateFlow<ExoPlayer?>(null)
    val player = _player.asStateFlow()

    // ExoPlayer y componentes
    private var trackSelector: DefaultTrackSelector? = null
    private val statsListener: PlaybackStatsListener by lazy {PlaybackStatsListener(false) { _, _ -> }}

    // Controllers
    private var watermarkController = FingerprintController()
    private var resizeManager: VideoResizeManagerWithTicker? = null

    // Jobs para handlers periódicos
    private var qosJob: Job? = null
    private var bookmarkJob: Job? = null
    private var heartbeatJob: Job? = null
    private var channelInputJob: Job? = null

    // Variables de estado interno
    private var livePlayingURL = ""
    private var livePlayingDRMURL = ""
    private var contentID = ""
    private var adjustedTSTVDate = Date()
    private var hasSeeked = true

    private var playableUrl : String? = null
    private var playableLicenseUrl : String? = null

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
                    getPlayableUrlUseCase(content.deliveryURL, content.getDRMString())
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
                    playableUrl = playableUrlResult.getOrNull()
                    playableLicenseUrl = drmUrlResult.getOrNull()

                    setPlayerUrls()
                } else {
                    val error = playableUrlResult.exceptionOrNull() ?: drmUrlResult.exceptionOrNull()
                    logger.error(TAG, "initializeWithContent getPlayableUrlUseCase error = ${error?.message}")

                    _uiState.value = _uiState.value.copy(error = error?.message)
                }
            }.onFailure { exception ->
                _uiState.value = _uiState.value.copy(error = exception.message)
            }
        }
    }

    private fun initializePlayerInternal() {
        val state = _uiState.value
        val content = _content.value

        // Guardar URLs live
//        if (content?.isLive == true && state.videoURL?.contains("vxwizard") != true) {
//            livePlayingURL = state.videoURL ?: ""
//            livePlayingDRMURL = state.licenseURL ?: ""
//        }

        // Crear y configurar player
        createPlayer()
    }

    private fun releasePlayer() {
        _player.value?.release()
        _player.value = null
    }

    private fun createPlayer() {

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
                if (playbackState == Player.STATE_READY && playWhenReady) {
                    _uiState.value = _uiState.value.copy(hidePreview = true)
                }

                // Actualizar posición y duración
                updatePlayerPosition()
            }
        })

    }

    private fun setPlayerUrls() {
        val state = _uiState.value
        val player = _player.value
        val content = _content.value

        var requestHeaders = emptyMap<String, String>()

        getJwTokenUseCase(_content.value).onSuccess { token ->
            requestHeaders = hashMapOf("Authorization" to "Bearer $token")
        }

        val mediaItem = MediaItem.Builder()
            .setUri(playableUrl)
            .setMediaMetadata(MediaMetadata.Builder().setTitle("").build())
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .setDrmConfiguration(
                MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                    .setLicenseUri(playableLicenseUrl)
                    .setLicenseRequestHeaders(requestHeaders)
                    .setMultiSession(true)
                    .build()
            ).build()

        val dataSourceFactory = tokenParamDataSourceFactory

        val mediaSource = DashMediaSource.Factory(dataSourceFactory)
            .createMediaSource(mediaItem)

        player?.setMediaSource(mediaSource)

        if ((content?.initialPlayPositionMs ?: 0) > 0 ) {
            player?.seekTo(content?.initialPlayPositionMs ?: 0)
        } else {
            player?.seekTo(0)
        }

//        if (state.showVideoLoop) {
//            player?.repeatMode = Player.REPEAT_MODE_ONE
//        }

        player?.prepare()
        player?.playWhenReady = true
    }

    private fun startPeriodicFunctions() {
//        startHeartbeat()
//        startQoSReporting()
//        startBookmarkReporting()
//        startWatermarking()
//        startTickerService()
    }
//
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
//        qosJob?.cancel()
//        bookmarkJob?.cancel()
//        watermarkController.stop()
//        viewModelScope.launch {
//            manageTickerUseCase.stopTicker()
//        }
    }

    private fun updatePlayerPosition() {
        _uiState.value = _uiState.value.copy(
            currentPosition = _player.value?.currentPosition ?: 0,
            duration = _player.value?.duration ?: 0
        )
    }
//
//    // Controles del player
//    fun togglePlayPause() {
//        _player.value?.let {
//            if (it.isPlaying) it.pause() else it.play()
//        }
//    }
//
//    fun handleFastForward() {
//        _uiState.value = _uiState.value.copy(showPreview = true)
//        _player.value?.seekForward()
//    }
//
//    fun handleRewind() {
//        _uiState.value = _uiState.value.copy(showPreview = true)
//        _player.value?.seekBack()
//    }
//
//    fun showControls() {
//        _uiState.value = _uiState.value.copy(showControls = true)
//    }
//
//    // Zapping
//    fun zapUp() {
//        if (_uiState.value.isLive) {
//            _uiState.value = _uiState.value.copy(isZapping = true)
//            viewModelScope.launch {
//                channelZapUseCase(increment = true)
//            }
//        }
//    }
//
//    fun zapDown() {
//        if (_uiState.value.isLive) {
//            _uiState.value = _uiState.value.copy(isZapping = true)
//            viewModelScope.launch {
//                channelZapUseCase(increment = false)
//            }
//        }
//    }
//
//    fun handleKeyUp() {
//        val showingControls = _uiState.value.showControls
//        if (!showingControls) {
//            zapUp()
//        }
//    }
//
//    fun handleKeyDown() {
//        val showingControls = _uiState.value.showControls
//        if (!showingControls) {
//            zapDown()
//        }
//    }
//
//    fun addChannelDigit(digit: String) {
//        if (_uiState.value.isLive) {
//            val currentDigits = _uiState.value.channelZapNumber + digit
//            _uiState.value = _uiState.value.copy(
//                channelZapNumber = currentDigits,
//                showChannelZapDisplay = true
//            )
//
//            channelInputJob?.cancel()
//            channelInputJob = viewModelScope.launch {
//                delay(2000)
//                if (currentDigits.isNotEmpty()) {
//                    changeToChannel(currentDigits.toInt() - 1)
//                    _uiState.value = _uiState.value.copy(
//                        channelZapNumber = "",
//                        showChannelZapDisplay = false
//                    )
//                }
//            }
//        }
//    }
//
//    private suspend fun changeToChannel(position: Int) {
//        _uiState.value = _uiState.value.copy(isZapping = true)
//        // Implementar cambio de canal usando UseCase
//    }
//
//    // Diálogos
//    fun showTrackSelection() {
//        _uiState.value = _uiState.value.copy(showTrackSelectionDialog = true)
//    }
//
//    fun hideTrackSelectionDialog() {
//        _uiState.value = _uiState.value.copy(showTrackSelectionDialog = false)
//    }
//
//    fun showSubtitlesDialog() {
//        _uiState.value = _uiState.value.copy(showSubtitlesDialog = true)
//    }
//
//    fun hideSubtitlesDialog() {
//        _uiState.value = _uiState.value.copy(showSubtitlesDialog = false)
//    }
//
//    fun showAudioTrackDialog() {
//        _uiState.value = _uiState.value.copy(showAudioTrackDialog = true)
//    }
//
//    fun hideAudioTrackDialog() {
//        _uiState.value = _uiState.value.copy(showAudioTrackDialog = false)
//    }
//
//    fun showPINDialog() {
//        _uiState.value = _uiState.value.copy(showPINDialog = true)
//    }
//
//    fun hidePINDialog() {
//        _uiState.value = _uiState.value.copy(showPINDialog = false)
//    }
//
//    fun validatePIN(pin: String) {
//        viewModelScope.launch {
//            validatePINUseCase(pin).fold(
//                onSuccess = { isValid ->
//                    _uiState.value = _uiState.value.copy(
//                        showPINDialog = false,
//                        isPINValid = isValid
//                    )
//                },
//                onFailure = { error ->
//                    _uiState.value = _uiState.value.copy(error = error.message)
//                }
//            )
//        }
//    }
//
//    // TSTV (Timeshift TV)
//    fun goToLive() {
//        _uiState.value = _uiState.value.copy(tstvMode = false)
//        setContent(livePlayingURL, livePlayingDRMURL)
//        _uiState.value = _uiState.value.copy(showLiveIndicator = true)
//    }
//
//    fun goToBeginning() {
//        setTSTVPoint(1, comesFromRestartButton = true)
//        _uiState.value = _uiState.value.copy(showLiveIndicator = false)
//    }
//
//    fun onScrubStart() {
//        // PreviewBar scrub start
//    }
//
//    fun onScrubMove(progress: Int, fromUser: Boolean) {
//        // PreviewBar scrub move
//    }
//
//    fun onScrubStop(progress: Int) {
//        val state = _uiState.value
//        val player = _player.value
//
//        if (state.isLive && state.eventHourEnd != null) {
//            val currentTimeMillis = System.currentTimeMillis()
//            val differenceNowCurrentProgress =
//                currentTimeMillis - state.eventHourBegin!!.time - progress
//
//            if (System.currentTimeMillis() >= state.eventHourEnd.time) {
//                viewModelScope.launch {
//                    getNewLiveEventInfoUseCase()
//                }
//            } else if (differenceNowCurrentProgress < 60000) {
//                goToLive()
//            } else {
//                setTSTVPoint(progress)
//                _uiState.value = _uiState.value.copy(showLiveIndicator = false)
//            }
//        }
//        player?.playWhenReady = true
//    }
//
//    private fun setTSTVPoint(progress: Int, comesFromRestartButton: Boolean = false) {
//        val state = _uiState.value
//        val currentTimeMillis = System.currentTimeMillis()
//        var differenceNowCurrentProgress =
//            currentTimeMillis - state.eventHourBegin!!.time - progress
//        val maxHours: Long = 2
//
//        if (differenceNowCurrentProgress > (maxHours * 60 * 60 * 1000)) {
//            differenceNowCurrentProgress = (maxHours * 60 * 60 * 1000) - 1000
//        }
//
//        adjustedTSTVDate = Date(currentTimeMillis - differenceNowCurrentProgress)
//
//        val dateFormat = SimpleDateFormat("yyyy/MM/dd/HH/mm", Locale.getDefault())
//        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
//        val formattedDate = dateFormat.format(adjustedTSTVDate)
//
//        _uiState.value = _uiState.value.copy(
//            tstvMode = true,
//            tstvPoint = adjustedTSTVDate
//        )
//
//        val tstvData = TSTVData(
//            tstvOffset = formattedDate,
//            progressMilliseconds = progress,
//            comesFromRestartButton = comesFromRestartButton
//        )
//
//        viewModelScope.launch {
//            // Llamar UseCase para TSTV mode
//        }
//    }
//
//    // Preview/Thumbnail loading
//    fun loadPreview(currentPosition: Long, max: Long) {
//        if (player != null && player!!.isPlaying) {
//            player!!.playWhenReady = false
//        }
//
//        val thumbnailUrl = buildThumbnailUrl(currentPosition)
//        _uiState.value = _uiState.value.copy(
//            thumbnailUrl = thumbnailUrl,
//            thumbnailPosition = currentPosition.mod(500000).toLong()
//        )
//    }
//
//    private fun buildThumbnailUrl(position: Long): String {
//        val playingURL = _uiState.value.videoURL ?: return ""
//
//        contentID = when {
//            playingURL.contains("smil:") -> playingURL.substringAfter("smil:").substringBefore("_")
//            playingURL.contains("nopack03-") -> playingURL.substringAfter("nopack03-")
//                .substringBefore("/")
//
//            else -> playingURL.substringAfter("nopack-").substringBefore("/")
//        }
//
//        val subtractZeros = (floor((position / 500000).toDouble()) + 1).toInt()
//        val numberOfZeros = 3 - subtractZeros.toString().length
//        val thumbnailNumberString = "0".repeat(numberOfZeros) + subtractZeros.toString()
//
//        return when {
//            playingURL.contains("smil:") ->
//                playingURL.substringBefore("/smil:") + "-img/" + contentID + "_mf" + thumbnailNumberString + ".jpg"
//
//            playingURL.contains("nopack03-") ->
//                playingURL.substringBefore("/nopack03-") + "-img/" + contentID + "_mf" + thumbnailNumberString + ".jpg"
//
//            else ->
//                playingURL.substringBefore("/nopack-") + "-img/" + contentID + "_mf" + thumbnailNumberString + ".jpg"
//        }
//    }
//
//    // Content management
//    fun handleNewContent(call: ContentUpdateData) {
//        viewModelScope.launch {
//            setNewContentUseCase(call).fold(
//                onSuccess = {
//                    // Actualizar state con la nueva configuración
//                    _uiState.value = _uiState.value.copy(
//                        videoURL = call.videoURL ?: _uiState.value.videoURL,
//                        licenseURL = call.licenseURL ?: _uiState.value.licenseURL,
//                        eventChannelName = call.eventChannelName ?: _uiState.value.eventChannelName,
//                        eventTitle = call.eventTitle ?: _uiState.value.eventTitle,
//                        isTimeshift = call.isTimeshift ?: _uiState.value.isTimeshift,
//                        eventBeginEnd = call.eventBeginEnd ?: _uiState.value.eventBeginEnd
//                    )
//
//                    // Actualizar URLs live si vienen en la llamada
//                    call.videoURL?.let { livePlayingURL = it }
//                    call.licenseURL?.let { livePlayingDRMURL = it }
//
//                    // Configurar nuevo contenido
//                    if (call.videoURL != null && call.licenseURL != null) {
//                        tokenRepository.storeK1KeyEncrypted(call.videoURL).also {
//                            setContent(call.videoURL, call.licenseURL)
//                        }
//                    }
//
//                    // Actualizar fechas TSTV si vienen
//                    call.eventBeginEnd?.let { setupTSTVDates(it) }
//                },
//                onFailure = { error ->
//                    _uiState.value = _uiState.value.copy(error = error.message)
//                }
//            )
//        }
//    }
//
//    fun handleTSTVMode(call: TSTVModeData) {
//        hasSeeked = false
//        viewModelScope.launch {
//            _uiState.value = _uiState.value.copy(
//                initialPlayPositionInMs = call.initialPlayPositionInMs,
//                videoURL = call.videoURL,
//                licenseURL = call.licenseURL
//            )
//
//            tokenRepository.storeK1KeyEncrypted(call.videoURL).also {
//                setContent(call.videoURL, call.licenseURL)
//            }
//        }
//    }
//
//    fun handleTickerUpdate(tickers: List<Ticker>) {
//        viewModelScope.launch {
//            manageTickerUseCase.updateTickers(tickers).fold(
//                onSuccess = {
//                    _uiState.value = _uiState.value.copy(tickers = tickers)
//                },
//                onFailure = { error ->
//                    _uiState.value = _uiState.value.copy(error = error.message)
//                }
//            )
//        }
//    }
//
//    fun setupWatermarkController(
//        playerContainer: ConstraintLayout?,
//        playerFrameLayout: FrameLayout?
//    ) {
//        if (playerContainer != null && playerFrameLayout != null) {
//            watermarkController.setup(playerContainer, playerFrameLayout, _uiState.value.userID)
//        }
//    }
//
//    fun setupResizeManager(
//        fragment: Fragment,
//        frameLayoutId: Int,
//        tickers: List<com.mamm.mammapps.data.model.player.Ticker>
//    ) {
//        resizeManager = VideoResizeManagerWithTicker(
//            fragment = fragment,
//            frameLayoutId = frameLayoutId,
//            tickerList = tickers
//        )
//        val state = _uiState.value
//        resizeManager?.setAutoResize(
//            enabled = state.showTickers(),
//            intervalSecs = tickers.firstOrNull()?.tiempoEntreApariciones?.toLong() ?: 30,
//            smallDurationSecs = tickers.firstOrNull()?.tiempoDuracion?.toLong() ?: 5
//        )
//    }
//
    private fun handlePlayerError(error: PlaybackException, context: Context) {
        val message = when {
            error.message?.contains("DRM") == true && context.packageName == "goandgo.openstream.com" ->
                "Contenido no disponible. Consulte opciones de compra en www.goandgotv.com"

            else -> "Error: ${error.message ?: ""}, code: ${error.errorCode}"
        }
        _uiState.value = _uiState.value.copy(error = message)
    }

//    private fun setContent(contentURL: String, drmURL: String) {
//        val player = _player.value
//        player?.stop()
//        player?.removeAnalyticsListener(statsListener)
//
//        _uiState.value = _uiState.value.copy(showVideoLoop = false)
//
//        stopPeriodicFunctions()
//        configurePlayerContent(contentURL, drmURL)
//    }

    // Método para manejar eventos de Compose
    fun handleEvent(event: VideoPlayerEvent) {
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

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    override fun onCleared() {
        super.onCleared()

        val player = _player.value

        qosJob?.cancel()
        bookmarkJob?.cancel()
        heartbeatJob?.cancel()
        channelInputJob?.cancel()

        watermarkController.stop()
        resizeManager?.release()

        player?.removeAnalyticsListener(statsListener)
        releasePlayer()
    }
}

// Eventos del VideoPlayer para Compose
sealed class VideoPlayerEvent {
    object TogglePlayPause : VideoPlayerEvent()
    object FastForward : VideoPlayerEvent()
    object Rewind : VideoPlayerEvent()
    object ToggleControls : VideoPlayerEvent()
    object Close : VideoPlayerEvent()
    object ShowSettings : VideoPlayerEvent()
    object ShowSubtitles : VideoPlayerEvent()
    object ShowAudioTracks : VideoPlayerEvent()
    object GoToLive : VideoPlayerEvent()
    object GoToBeginning : VideoPlayerEvent()
    object HidePINDialog : VideoPlayerEvent()
    object SeekFinished : VideoPlayerEvent()
    data class ValidatePIN(val pin: String) : VideoPlayerEvent()
    data class SeekTo(val position: Long) : VideoPlayerEvent()
}
