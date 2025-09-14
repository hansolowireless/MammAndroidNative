//package com.mamm.mammapps.ui.fragment
//
//import android.content.Context
//import android.media.AudioManager
//import android.os.Bundle
//import android.view.KeyEvent
//import android.view.LayoutInflater
//import android.view.MotionEvent
//import android.view.View
//import android.view.ViewGroup
//import android.widget.FrameLayout
//import androidx.constraintlayout.widget.ConstraintLayout
//import androidx.core.content.ContextCompat
//import androidx.core.view.GestureDetectorCompat
//import androidx.fragment.app.Fragment
//import androidx.lifecycle.lifecycleScope
//import com.bumptech.glide.Glide
//import com.mamm.mammapps.ui.component.player.dialogs.DefaultDialogHelper
//import com.github.rubensousa.previewseekbar.PreviewBar
//import com.github.rubensousa.previewseekbar.PreviewLoader
//import com.mamm.mammapps.R
//import com.mamm.mammapps.data.model.player.VideoPlayerUiState
//import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
//import com.mamm.mammapps.ui.viewmodel.PlayerViewModel
//import dagger.hilt.android.AndroidEntryPoint
//import kotlinx.coroutines.launch
//import kotlin.math.abs
//
//@AndroidEntryPoint
//class VideoPlayerFragment : Fragment(), PreviewLoader, PreviewBar.OnScrubListener,
//    View.OnClickListener, PINDialogFragment.PINDialogListener,
//    ShouldEnterPINDialogFragment.ShouldEnterPINDialogListener {
//
//    private val viewModel: PlayerViewModel by viewModels()
//    private var binding: ActivityVideoPlayerThumbnailMobileBinding? = null
//
//    // Gesture detection
//    private lateinit var mDetector: GestureDetectorCompat
//
//    // Dialogs
//    private lateinit var shouldEnterPINDialog: ShouldEnterPINDialogFragment
//    private lateinit var pinDialog: PINDialogFragment
//
//    // Configuration
//    private var isAndroidTV: Boolean = false
//
//    companion object {
//        private const val TAG = "Player"
//        private const val ARG_CONFIG = "video_config"
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        binding = ActivityVideoPlayerThumbnailMobileBinding.inflate(inflater, container, false)
//        return binding!!.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        setupUI()
//        observeViewModel()
//
//        // Inicializar player con ViewModel
//        viewModel.initializePlayer(requireContext())
//    }
//
//    private fun setupUI() {
//        isAndroidTV = activity?.packageManager?.hasSystemFeature(FEATURE_LEANBACK) ?: true
//
//        binding?.apply {
//            // Preview TimeBar setup
//            val previewTimeBar = exoProgress as CustomPreviewBar
//            previewTimeBar.scrubberDrawable =
//                ContextCompat.getDrawable(requireContext(), R.drawable.custom_scrubber_exoplayer)
//            previewTimeBar.addOnScrubListener(this@VideoPlayerFragment)
//            previewTimeBar.setPreviewLoader(this@VideoPlayerFragment)
//            previewTimeBar.setKeyTimeIncrement(30000)
//
//            // Buttons setup
//            closeButton.setOnClickListener(this@VideoPlayerFragment)
//            selectTracksButton.setOnClickListener(this@VideoPlayerFragment)
//            subtitlesButton.setOnClickListener(this@VideoPlayerFragment)
//            audioTracksButton.setOnClickListener(this@VideoPlayerFragment)
//            goLiveButton.setOnClickListener(this@VideoPlayerFragment)
//            goBeginningButton.setOnClickListener(this@VideoPlayerFragment)
//            devModeShowURlsButton.setOnClickListener(this@VideoPlayerFragment)
//
//            // Color filters for buttons
//            val primaryDarkColor = resources.getColor(R.color.colorPrimaryDark)
//            selectTracksButton.setColorFilter(primaryDarkColor, PorterDuff.Mode.SRC_IN)
//            subtitlesButton.setColorFilter(primaryDarkColor, PorterDuff.Mode.SRC_IN)
//            audioTracksButton.setColorFilter(primaryDarkColor, PorterDuff.Mode.SRC_IN)
//            goLiveButton.setColorFilter(primaryDarkColor, PorterDuff.Mode.SRC_IN)
//            goBeginningButton.setColorFilter(primaryDarkColor, PorterDuff.Mode.SRC_IN)
//
//            // ExoPlayer control buttons
//            setupPlayerControls()
//
//            // Visibility setup
//            closeButton.isVisible = !isAndroidTV
//            devModeShowURlsButton.isVisible = config.isDevModeOn
//        }
//
//        setupGestureDetection()
//        setupKeyDetection()
//        adaptConstraints()
//        requestFullScreenIfLandscape()
//    }
//
//    private fun setupPlayerControls() {
//        binding?.apply {
//            exoFfwd.setOnClickListener { viewModel.handleFastForward() }
//            exoRew.setOnClickListener { viewModel.handleRewind() }
//            exoPlayPause.setOnClickListener { viewModel.togglePlayPause() }
//        }
//    }
//
//    private fun observeViewModel() {
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.uiState.collect { state ->
//                updateUI(state)
//            }
//        }
//    }
//
//    private fun updateUI(state: VideoPlayerUiState) {
//        binding?.apply {
//            // Player
//            playerView.player = state.player
//
//            // Configuration
//            state.config?.let { config ->
//                channelOrTitleLabel.text = config.eventChannelName
//                titleLabel.text = config.eventTitle
//
//                // Setup content image
//                Glide.with(this@VideoPlayerFragment)
//                    .load(config.logoURL)
//                    .into(contentImageView)
//
//                // Setup TSTV dates if live
//                if (config.isLive) {
//                    val previewTimeBar = exoProgress as CustomPreviewBar
//                    previewTimeBar.setEventHourBegin(state.eventHourBegin)
//                    previewTimeBar.setEventHourEnd(state.eventHourEnd)
//                    previewTimeBar.setIsTimeshift(config.isTimeshift)
//                }
//
//                updateViewsForContentType(config)
//            }
//
//            // TSTV state
//            if (state.tstvMode) {
//                val previewTimeBar = exoProgress as CustomPreviewBar
//                previewTimeBar.setTstvMode(true)
//                state.tstvPoint?.let { previewTimeBar.setTstvPoint(it) }
//            } else {
//                val previewTimeBar = exoProgress as CustomPreviewBar
//                previewTimeBar.setTstvMode(false)
//            }
//
//            // TSTV hour begin text
//            if (state.tstvHourBeginText.isNotEmpty()) {
//                tstvHourbegin.text = state.tstvHourBeginText
//            }
//
//            // Live indicator
//            updateLiveIndicator(state.showLiveIndicator)
//
//            // Zapping display
//            channelZapNumber.text = state.channelZapNumber
//            channelNumberZapDisplay.isVisible = state.showChannelZapDisplay
//
//            // Zapping mode
//            if (state.isZapping) {
//                hideViewsForZapping()
//            }
//
//            // Preview control
//            if (state.hidePreview) {
//                val previewTimeBar = exoProgress as CustomPreviewBar
//                previewTimeBar.hidePreview()
//            }
//            if (state.showPreview) {
//                val previewTimeBar = exoProgress as CustomPreviewBar
//                previewTimeBar.showPreview()
//            }
//
//            // Thumbnail loading
//            if (state.thumbnailUrl.isNotEmpty()) {
//                Glide.with(requireContext())
//                    .load(state.thumbnailUrl)
//                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
//                    .transform(GlideThumbnailTransformation(state.thumbnailPosition))
//                    .into(imageView)
//            }
//
//            // Error handling
//            state.error?.let { error ->
//                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
//            }
//
//            // Controls visibility
//            if (state.showControls && !playerView.isControllerFullyVisible) {
//                playerView.showController()
//            }
//
//            // Track buttons visibility
//            updateTrackButtonsVisibility(state.player)
//        }
//
//        // Handle dialogs
//        handleDialogs(state)
//    }
//
//    private fun handleDialogs(state: VideoPlayerUiState) {
//        if (state.showPINDialog) {
//            showPINDialog()
//        }
//
//        if (state.showTrackSelectionDialog) {
//            showTrackSelectionDialog()
//        }
//
//        if (state.showSubtitlesDialog) {
//            showSubtitlesDialog()
//        }
//
//        if (state.showAudioTrackDialog) {
//            showAudioTrackDialog()
//        }
//    }
//
//    private fun updateViewsForContentType(config: VideoPlayerActivityConfig) {
//        binding?.apply {
//            if (config.isLive) {
//                // Remove navigation buttons for live content
//                val buttonsLayout = controlsLayout
//                buttonsLayout.removeView(exoPrev)
//                buttonsLayout.removeView(exoNext)
//
//                if (!config.isTimeshift) {
//                    // Hide progress controls for non-timeshift live
//                    exoProgress.isVisible = false
//                    exoPosition.isVisible = false
//                    exoDuration.isVisible = false
//                    tstvHourbegin.isVisible = false
//                    goLiveButton.isVisible = false
//                    goBeginningButton.isVisible = false
//                } else {
//                    // Show TSTV controls
//                    exoProgress.isVisible = true
//                    tstvHourbegin.isVisible = true
//                    goLiveButton.isVisible = true
//                    goBeginningButton.isVisible = true
//                    exoDuration.isVisible = false
//                    exoPosition.isVisible = false
//                }
//
//                // Remove fast forward/rewind for live
//                buttonsLayout.removeView(exoRew)
//                buttonsLayout.removeView(exoFfwd)
//            } else {
//                // VOD content - show all controls
//                exoProgress.isVisible = true
//                exoPosition.isVisible = true
//                exoDuration.isVisible = true
//                tstvHourbegin.isVisible = false
//                goLiveButton.isVisible = false
//                goBeginningButton.isVisible = false
//            }
//
//            // Radio content - show artwork fullscreen
//            if (config.isRadio) {
//                showFullscreenArtwork(config.logoURL)
//            }
//        }
//    }
//
//    private fun updateLiveIndicator(showLive: Boolean) {
//        binding?.apply {
//            val params = liveIndicator.layoutParams
//            params.width = if (showLive) 150 else 1
//            liveIndicator.layoutParams = params
//            liveIndicator.setMinimumWidth(0)
//            liveIndicator.requestLayout()
//
//            val color = if (showLive) {
//                ContextCompat.getColor(requireContext(), android.R.color.holo_red_light)
//            } else {
//                ContextCompat.getColor(requireContext(), android.R.color.black)
//            }
//            liveIndicator.setTextColor(color)
//
//            goLiveButton.isVisible = !showLive
//        }
//    }
//
//    private fun updateTrackButtonsVisibility(player: ExoPlayer?) {
//        binding?.apply {
//            audioTracksButton.isVisible = player?.let {
//                TrackSelectionDialog.willHaveAudioContent(it)
//            } ?: false
//
//            subtitlesButton.isVisible = player?.let {
//                TrackSelectionDialog.willHaveCCContent(it)
//            } ?: false
//        }
//    }
//
//    private fun hideViewsForZapping() {
//        binding?.apply {
//            val viewsToHide = listOf(
//                exoProgress, exoPosition, exoDuration, exoPrev, exoRew,
//                exoFfwd, goLiveButton, goBeginningButton, subtitlesButton,
//                selectTracksButton, audioTracksButton, exoPlayPause, tstvHourbegin
//            )
//
//            viewsToHide.forEach { it.isVisible = false }
//
//            updateLiveIndicator(false)
//            controlsLayout.setBackgroundColor(Color.TRANSPARENT)
//            wholePlayerBackground.setBackgroundColor(Color.TRANSPARENT)
//            playerView.controllerShowTimeoutMs = 1000
//        }
//    }
//
//    private fun showFullscreenArtwork(logoURL: String) {
//        binding?.apply {
//            val artworkView = playerView.findViewById<ImageView>(
//                com.google.android.exoplayer2.ui.R.id.exo_artwork
//            )
//
//            playerView.videoSurfaceView?.isVisible = false
//            artworkView?.apply {
//                isVisible = true
//                scaleType = ImageView.ScaleType.CENTER_CROP
//                layoutParams = FrameLayout.LayoutParams(
//                    FrameLayout.LayoutParams.MATCH_PARENT,
//                    FrameLayout.LayoutParams.MATCH_PARENT
//                )
//
//                Glide.with(context)
//                    .load(logoURL)
//                    .transition(DrawableTransitionOptions.withCrossFade())
//                    .centerCrop()
//                    .into(this)
//            }
//        }
//    }
//
//    private fun setupGestureDetection() {
//        if (!isAndroidTV) {
//            mDetector = GestureDetectorCompat(requireContext(), object : GestureDetector.SimpleOnGestureListener() {
//                override fun onFling(
//                    downEvent: MotionEvent?,
//                    moveEvent: MotionEvent,
//                    velocityX: Float,
//                    velocityY: Float
//                ): Boolean {
//                    downEvent ?: return false
//
//                    val diffY = moveEvent.y - downEvent.y
//                    val diffX = moveEvent.x - downEvent.x
//
//                    if (abs(diffX) > abs(diffY)) {
//                        if (diffX > 0) viewModel.zapUp() else viewModel.zapDown()
//                    } else {
//                        if (diffY > 0) viewModel.zapDown() else viewModel.zapUp()
//                    }
//                    return true
//                }
//
//                override fun onDown(e: MotionEvent): Boolean {
//                    viewModel.showControls()
//                    return true
//                }
//            })
//
//            binding?.playerView?.setOnTouchListener { _, event ->
//                mDetector.onTouchEvent(event)
//                true
//            }
//        }
//    }
//
//    private fun setupKeyDetection() {
//        view?.apply {
//            isFocusableInTouchMode = true
//            requestFocus()
//            setOnKeyListener { _, keyCode, _ ->
//                handleKeyEvent(keyCode)
//            }
//        }
//    }
//
//    private fun handleKeyEvent(keyCode: Int): Boolean {
//        return when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_CHANNEL_UP -> {
//                viewModel.handleKeyUp()
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_DOWN, KeyEvent.KEYCODE_CHANNEL_DOWN -> {
//                viewModel.handleKeyDown()
//                true
//            }
//            KeyEvent.KEYCODE_DPAD_CENTER -> {
//                viewModel.showControls()
//                true
//            }
//            in KeyEvent.KEYCODE_0..KeyEvent.KEYCODE_9 -> {
//                val digit = (keyCode - KeyEvent.KEYCODE_0).toString()
//                viewModel.addChannelDigit(digit)
//                true
//            }
//            in KeyEvent.KEYCODE_NUMPAD_0..KeyEvent.KEYCODE_NUMPAD_9 -> {
//                val digit = (keyCode - KeyEvent.KEYCODE_NUMPAD_0).toString()
//                viewModel.addChannelDigit(digit)
//                true
//            }
//            KeyEvent.KEYCODE_VOLUME_UP, KeyEvent.KEYCODE_VOLUME_DOWN -> {
//                handleVolumeKeys(keyCode)
//                true
//            }
//            else -> false
//        }
//    }
//
//    private fun handleVolumeKeys(keyCode: Int) {
//        val audioManager = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requireContext().getSystemService(AudioManager::class.java)
//        } else {
//            requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
//        }
//
//        when (keyCode) {
//            KeyEvent.KEYCODE_VOLUME_UP -> audioManager.adjustStreamVolume(
//                AudioManager.STREAM_MUSIC,
//                AudioManager.ADJUST_RAISE,
//                AudioManager.FLAG_SHOW_UI
//            )
//            KeyEvent.KEYCODE_VOLUME_DOWN -> audioManager.adjustStreamVolume(
//                AudioManager.STREAM_MUSIC,
//                AudioManager.ADJUST_LOWER,
//                AudioManager.FLAG_SHOW_UI
//            )
//        }
//    }
//
//    private fun adaptConstraints() {
//        if (!isAndroidTV) {
//            val windowMetrics = WindowMetricsCalculator.getOrCreate()
//                .computeCurrentWindowMetrics(requireActivity())
//            val height = windowMetrics.bounds.height()
//            val width = windowMetrics.bounds.width()
//
//            binding?.root?.let { rootView ->
//                if (width / height > 16 / 9) {
//                    (rootView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "W,16:9"
//                } else {
//                    (rootView.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "H,16:9"
//                }
//            }
//        } else {
//            binding?.previewFrameLayout?.let { thumbView ->
//                (thumbView.layoutParams as ConstraintLayout.LayoutParams)
//                    .matchConstraintPercentWidth = 0.20F
//            }
//        }
//    }
//
//    private fun requestFullScreenIfLandscape() {
//        activity?.window?.decorView?.systemUiVisibility = (
//                View.SYSTEM_UI_FLAG_IMMERSIVE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
//                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                        or View.SYSTEM_UI_FLAG_FULLSCREEN
//                )
//    }
//
//    // Click handlers
//    override fun onClick(view: View) {
//        when (view) {
//            binding?.selectTracksButton -> viewModel.showTrackSelection()
//            binding?.subtitlesButton -> viewModel.showSubtitlesDialog()
//            binding?.audioTracksButton -> viewModel.showAudioTrackDialog()
//            binding?.goLiveButton -> viewModel.goToLive()
//            binding?.goBeginningButton -> viewModel.goToBeginning()
//            binding?.closeButton -> activity?.onBackPressedDispatcher?.onBackPressed()
//            binding?.devModeShowURlsButton -> {
//                DefaultDialogHelper(requireContext()).showInfoDialog(
//                    "URL de Contenido",
//                    config.videoURL
//                )
//            }
//        }
//    }
//
//    // PreviewBar.OnScrubListener
//    override fun onScrubStart(previewBar: PreviewBar?) {
//        viewModel.onScrubStart()
//    }
//
//    override fun onScrubMove(previewBar: PreviewBar?, progress: Int, fromUser: Boolean) {
//        viewModel.onScrubMove(progress, fromUser)
//    }
//
//    override fun onScrubStop(previewBar: PreviewBar?) {
//        viewModel.onScrubStop(previewBar?.progress ?: 0)
//    }
//
//    // PreviewLoader
//    override fun loadPreview(currentPosition: Long, max: Long) {
//        viewModel.loadPreview(currentPosition, max)
//    }
//
//    // Dialog handlers
//    private fun showPINDialog() {
//        if (!::pinDialog.isInitialized) {
//            pinDialog = PINDialogFragment()
//            pinDialog.isCancelable = false
//        }
//        pinDialog.show(childFragmentManager, PINDialogFragment.TAG)
//    }
//
//    private fun showTrackSelectionDialog() {
//        val player = viewModel.uiState.value.player ?: return
//        if (TrackSelectionDialog.willHaveContent(player)) {
//            val dialog = TrackSelectionDialog.createForPlayer(player) {
//                viewModel.hideTrackSelectionDialog()
//            }
//            dialog.show(childFragmentManager, null)
//        }
//    }
//
//    private fun showSubtitlesDialog() {
//        val player = viewModel.uiState.value.player ?: return
//        if (TrackSelectionDialog.willHaveCCContent(player)) {
//            val dialog = TrackSelectionDialog.createCCDialogForPlayer(player) {
//                viewModel.hideSubtitlesDialog()
//            }
//            dialog.show(childFragmentManager, null)
//        }
//    }
//
//    private fun showAudioTrackDialog() {
//        val player = viewModel.uiState.value.player ?: return
//        if (TrackSelectionDialog.willHaveAudioContent(player)) {
//            val dialog = TrackSelectionDialog.createAudioTrackDialogForPlayer(player) {
//                viewModel.hideAudioTrackDialog()
//            }
//            dialog.show(childFragmentManager, null)
//        }
//    }
//
//    // PIN Dialog listeners
//    override fun onDialogPositiveClick(pin: String?) {
//        pin?.let { viewModel.validatePIN(it) }
//    }
//
//    override fun onDialogSWIPEDUP() {
//        viewModel.zapUp()
//        viewModel.hidePINDialog()
//    }
//
//    override fun onDialogSWIPEDDOWN() {
//        viewModel.zapDown()
//        viewModel.hidePINDialog()
//    }
//
//    // Should enter PIN dialog listeners
//    override fun onShouldPINDialogOKClick() {
//        viewModel.showPINDialog()
//    }
//
//    override fun onShouldPINDialogUPClick() {
//        viewModel.zapUp()
//    }
//
//    override fun onShouldPINDialogDOWNClick() {
//        viewModel.zapDown()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        binding = null
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        // El ViewModel se encarga de la limpieza en onCleared()
//    }
//}