package com.mamm.mammapps.ui.screen

import android.content.pm.ActivityInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.example.openstream_flutter_rw.ui.manager.watermark.FingerprintController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.data.logger.SimpleLogger
import com.mamm.mammapps.data.model.player.GlideThumbnailTransformation
import com.mamm.mammapps.ui.component.player.ZappingScreen
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.component.player.dialogs.TrackSelectionDialog
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.extension.buildThumbnailUrl
import com.mamm.mammapps.ui.extension.findActivity
import com.mamm.mammapps.ui.extension.insertThumbnail
import com.mamm.mammapps.ui.extension.isNumpadNumber
import com.mamm.mammapps.ui.extension.jump10sBack
import com.mamm.mammapps.ui.extension.jump10sForward
import com.mamm.mammapps.ui.extension.toDigitString
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.PlayerColor
import com.mamm.mammapps.ui.viewmodel.VideoPlayerViewModel
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
fun PlayerViewWithControlsExperimental(
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel,
    player: ExoPlayer?,
    content: ContentToPlayUI? = null
) {
    // --- ESTADOS Y REFERENCIAS ---
    val showZappingLayer by viewModel.showZappingLayer.collectAsStateWithLifecycle()
    val zappingInfo by viewModel.zappingInfo.collectAsStateWithLifecycle()
    val zappingNumberDisplay by viewModel.zappingNumberDisplay.collectAsStateWithLifecycle()
    val isTstvMode by viewModel.isTstvMode.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val view = LocalView.current
    val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

    val fingerprintController = remember { FingerprintController() }
    val playerViewRef = remember { mutableStateOf<StyledPlayerView?>(null) }

    val playerFocusRequester = remember { FocusRequester() }
    val zappingFocusRequester = remember { FocusRequester() }
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    // --- EFECTOS DE PANTALLA COMPLETA Y ORIENTACIÓN ---
    DisposableEffect(Unit) {
        val window = context.findActivity().window
        val insetsController = WindowCompat.getInsetsController(window, view)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }

    // Gestiona el cambio de foco cuando aparece/desaparece el zapping
    LaunchedEffect(showZappingLayer) {
        if (showZappingLayer) {
            zappingFocusRequester.requestFocus()
        } else {
            playerFocusRequester.requestFocus()
        }
    }

    // --- CONTENEDOR PRINCIPAL QUE APILA LAS VISTAS ---
    Box(
        modifier = modifier
            .fillMaxSize()
            .focusRequester(playerFocusRequester) // Foco por defecto para el reproductor
            .onKeyEvent { keyEvent ->
                val playerView = playerViewRef.value ?: return@onKeyEvent false

                if (keyEvent.type == KeyEventType.KeyDown) {
                    val isControllerVisible = playerView.isControllerFullyVisible

                    keyEvent.key.toDigitString()?.let { digit ->
                        viewModel.showZappingNumberDisplay(newDigit = digit)
                        return@onKeyEvent true
                    }

                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.DirectionLeft, Key.DirectionRight -> {
                            if (!isControllerVisible && !showZappingLayer) {
                                playerView.showController()
                                true // Consumir evento
                            } else {
                                false // Dejar pasar al sistema (ExoPlayer o ZappingScreen)
                            }
                        }

                        Key.DirectionUp, Key.DirectionDown, Key.ChannelUp, Key.ChannelDown -> {
                            if (!showZappingLayer && !isControllerVisible) {
                                viewModel.showZappingLayer()
                                true
                            } else {
                                false
                            }
                        }

                        else -> false
                    }
                } else false
            }
    ) {
        // --- CAPA 1: REPRODUCTOR DE VÍDEO (CON DETECCIÓN DE GESTOS) ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { change, dragAmount ->
                        // dragAmount es la cantidad de píxeles que se ha arrastrado.
                        // Positivo hacia abajo, negativo hacia arriba.

                        val playerView = playerViewRef.value ?: return@detectVerticalDragGestures
                        val isControllerVisible = playerView.isControllerFullyVisible

                        // Condiciones para abrir el zapping con swipe:
                        // 1. La capa de zapping NO debe estar visible.
                        // 2. Los controles del player NO deben estar visibles.
                        // 3. Debe haber un arrastre vertical significativo.
                        if (!showZappingLayer && !isControllerVisible && abs(dragAmount) > 1.0f) {
                            viewModel.showZappingLayer()
                            change.consume() // Consume el evento para que no se propague
                        }
                    }
                }
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { viewContext ->
                    // --- BLOQUE FACTORY (SE EJECUTA UNA VEZ) ---
                    val parentView = LayoutInflater.from(viewContext).inflate(
                        R.layout.activity_video_player_thumbnail_mobile,
                        FrameLayout(viewContext),
                        false
                    )

                    val playingContent = parentView.tag as? ContentToPlayUI

                    val styledPlayerView =
                        parentView.findViewById<StyledPlayerView>(R.id.player_view).also {
                            playerViewRef.value = it
                        }

                    val previewTimeBar =
                        parentView.findViewById<CustomPreviewBar>(R.id.exo_progress)
                    val audioTracksButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.audio_tracks_button)
                    val ccTracksButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.cc_tracks_button)
                    val videoQualityButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.select_tracks_button)
                    val startOverButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.go_beginning_button)
                    val returnToLivePointButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.go_live_button)
                    val jump10sback =
                        parentView.findViewById<AppCompatImageButton>(R.id.jump_10s_back)
                    val jump10sforward =
                        parentView.findViewById<AppCompatImageButton>(R.id.jump_10s_forward)
                    val closeButton =
                        parentView.findViewById<ImageButton>(R.id.close_button)

                    styledPlayerView.controllerAutoShow = false

                    previewTimeBar.setPreviewLoader { currentPosition, _ ->
                        parentView.findViewById<ImageView>(R.id.imageView)?.let {
                            Glide.with(it)
                                .load(playingContent?.deliveryURL?.buildThumbnailUrl(currentPosition))
                                .into(it)
                        }
                    }

                    val showTrackDialog: (TrackType) -> Unit = showTrackDialog@{ trackType ->
                        val currentPlayer = styledPlayerView.player ?: return@showTrackDialog
                        if (fragmentManager == null || fragmentManager.isStateSaved) return@showTrackDialog
                        try {
                            val dialog = when (trackType) {
                                TrackType.AUDIO -> TrackSelectionDialog.createAudioTrackDialogForPlayer(
                                    currentPlayer
                                ) {}

                                TrackType.CC -> TrackSelectionDialog.createCCDialogForPlayer(
                                    currentPlayer
                                ) {}

                                TrackType.VIDEO -> TrackSelectionDialog.createForPlayer(
                                    currentPlayer
                                ) {}
                            }
                            dialog.setStyle(
                                TrackSelectionDialog.STYLE_NORMAL,
                                R.style.TrackSelectionDialogThemeOverlay
                            )
                            dialog.show(fragmentManager, "${trackType.name}_track_dialog")
                        } catch (e: Exception) {
                            Log.e("TrackDialog", "Error showing ${trackType.name} dialog", e)
                        }
                    }

                    audioTracksButton.setOnClickListener { showTrackDialog(TrackType.AUDIO) }
                    ccTracksButton.setOnClickListener { showTrackDialog(TrackType.CC) }
                    videoQualityButton.setOnClickListener { showTrackDialog(TrackType.VIDEO) }

                    startOverButton.setOnClickListener {
                        viewModel.triggerTSTVMode(
                            previewTimeBar,
                            forcePosition = 0
                        )
                    }
                    returnToLivePointButton.setOnClickListener{
                        viewModel.setLivePosition(previewTimeBar)
                    }

                    jump10sback.setOnClickListener { styledPlayerView.player?.jump10sBack() }
                    jump10sforward.setOnClickListener { styledPlayerView.player?.jump10sForward() }

                    closeButton.setOnClickListener { backDispatcher?.onBackPressed() }

                    parentView
                },
                update = { parentView ->
                    val styledPlayerView =
                        parentView.findViewById<StyledPlayerView>(R.id.player_view)
                    val audioTracksButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.audio_tracks_button)
                    val ccTracksButton =
                        parentView.findViewById<AppCompatImageButton>(R.id.cc_tracks_button)
                    val previewTimeBar =
                        parentView.findViewById<CustomPreviewBar>(R.id.exo_progress)

                    Log.d("AndroidViewUpdate", "Update ejecutado. isTstvMode = $isTstvMode")

                    var playerChanged = false
                    if (styledPlayerView.player != player) {
                        styledPlayerView.player = player
                        if (player != null) {
                            styledPlayerView.requestLayout()
                        }
                        playerChanged = true
                    }

                    var contentChanged = false
                    if (parentView.tag != content) {
                        parentView.tag = content
                        contentChanged = true
                    }

                    if (playerChanged || contentChanged) {
                        (parentView as? ViewGroup)?.let { viewGroup ->
                            fingerprintController.setup(viewGroup, styledPlayerView)
                            fingerprintController.start(
                                fingerPrintInfo = content?.fingerprintInfo,
                                watermarkInfo = content?.watermarkInfo
                            )
                        }

                        previewTimeBar.isPreviewEnabled =
                            (content?.identifier is ContentIdentifier.VoD)
                        previewTimeBar.setPreviewLoader { currentPosition, _ ->
                            parentView.findViewById<ImageView>(R.id.imageView)?.insertThumbnail(
                                url = styledPlayerView.player?.currentMediaItem?.playbackProperties?.uri.toString(),
                                position = currentPosition,
                                onError = {
                                    previewTimeBar.isPreviewEnabled = false
                                }
                            )
                        }
                    }

                    viewModel.setControlVisibility(styledPlayerView)
                    viewModel.setDialogButtonVisibility(ccTracksButton, audioTracksButton)

                }
            )
        }

        // --- CAPA 2: PANTALLA DE ZAPPING ---
        if (showZappingLayer) {
            ZappingScreen(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .focusRequester(zappingFocusRequester) // Foco para el zapping
                    .focusable(),
                zappingInfo = zappingInfo,
                currentChannel = content,
                onChannelClick = { channel ->
                    viewModel.findAndPlayChannel(channel)
                },
                onDismiss = {
                    viewModel.hideZappingLayer()
                }
            )
        }

        LaunchedEffect (zappingNumberDisplay)
        {
            delay(PlayerConstant.CHANNEL_NUMBER_ZAPPING_WAITTIME)
            viewModel.navigateToChannel(zappingNumberDisplay)
        }

        if (zappingNumberDisplay.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .padding(top = Dimensions.paddingMedium, end = Dimensions.paddingMedium)
                    .align(Alignment.TopEnd)
                    .background(Color.Gray)
                    .padding(Dimensions.paddingSmall)
            ) {
                Text(
                    text = zappingNumberDisplay,
                    color = PlayerColor.channelZappingText,
                    style = MaterialTheme.typography.headlineLarge,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }


    }
}

private enum class TrackType { AUDIO, CC, VIDEO }
