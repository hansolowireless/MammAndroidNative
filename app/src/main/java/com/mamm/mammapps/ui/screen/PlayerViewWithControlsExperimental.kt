package com.mamm.mammapps.ui.screen

import android.content.pm.ActivityInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.Glide
import com.example.openstream_flutter_rw.ui.manager.watermark.FingerprintController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.component.player.dialogs.TrackSelectionDialog
import com.mamm.mammapps.ui.extension.buildThumbnailUrl
import com.mamm.mammapps.ui.extension.findActivity
import com.mamm.mammapps.ui.extension.jump10sBack
import com.mamm.mammapps.ui.extension.jump10sForward
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.viewmodel.VideoPlayerViewModel

@Composable
fun PlayerViewWithControlsExperimental(
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel,
    player: ExoPlayer?,
    content: ContentToPlayUI? = null
) {

    val showZappingLayer by viewModel.showZappingLayer.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager

    // Usamos remember { ... } para asegurarnos de que el controlador se cree una sola vez.
    val fingerprintController = remember { FingerprintController() }

    // Referencia mutable para la StyledPlayerView que no causa recomposiciones.
    val playerViewRef = remember { mutableStateOf<StyledPlayerView?>(null) }

    val view = LocalView.current

    DisposableEffect(Unit) {
        val window = context.findActivity().window
        val insetsController = WindowCompat.getInsetsController(window, view)

        insetsController.hide(WindowInsetsCompat.Type.systemBars())

        // Configurar el comportamiento para que las barras aparezcan temporalmente al deslizar desde el borde
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        }
    }

    DisposableEffect(Unit) {
        val activity = context.findActivity()
        val originalOrientation = activity.requestedOrientation

        // Forzar landscape solo en esta pantalla
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        onDispose {
            // Cuando salgas de esta pantalla, restaurar orientación original
            activity.requestedOrientation = originalOrientation
        }
    }

    AndroidView(
        modifier = modifier.fillMaxSize()
            .onKeyEvent { keyEvent ->
                val playerView = playerViewRef.value ?: return@onKeyEvent false

                if (keyEvent.type == KeyEventType.KeyDown) {
                    val isControllerVisible = playerView.isControllerFullyVisible
                    when (keyEvent.key) {
                        Key.DirectionCenter, Key.Enter, Key.DirectionLeft, Key.DirectionRight -> {
                            if (!isControllerVisible) {
                                playerView.showController()
                                true // Consumir evento
                            } else {
                                false // Dejar pasar a ExoPlayer
                            }
                        }
                        Key.DirectionUp, Key.DirectionDown -> {
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
            },
        factory = { viewContext ->
            // --- BLOQUE FACTORY (SE EJECUTA UNA VEZ) ---
            val parentView = LayoutInflater.from(viewContext).inflate(
                R.layout.activity_video_player_thumbnail_mobile,
                FrameLayout(viewContext),
                false
            )

            val styledPlayerView = parentView.findViewById<StyledPlayerView>(R.id.player_view).also {
                playerViewRef.value = it // Asignamos la referencia para el onKeyEvent
            }

            val previewTimeBar = parentView.findViewById<CustomPreviewBar>(R.id.exo_progress)
            val audioTracksButton = parentView.findViewById<AppCompatImageButton>(R.id.audio_tracks_button)
            val ccTracksButton = parentView.findViewById<AppCompatImageButton>(R.id.cc_tracks_button)
            val videoQualityButton = parentView.findViewById<AppCompatImageButton>(R.id.select_tracks_button)
            val startOverButton = parentView.findViewById<AppCompatImageButton>(R.id.go_beginning_button)
            val jump10sback = parentView.findViewById<AppCompatImageButton>(R.id.jump_10s_back)
            val jump10sforward = parentView.findViewById<AppCompatImageButton>(R.id.jump_10s_forward)

            previewTimeBar.setPreviewLoader { currentPosition, _ ->
                val currentContent = parentView.tag as? ContentToPlayUI
                parentView.findViewById<ImageView>(R.id.imageView)?.let {
                    Glide.with(it)
                        .load(currentContent?.deliveryURL?.buildThumbnailUrl(currentPosition))
                        .into(it)
                }
            }

            val showTrackDialog: (TrackType) -> Unit = showTrackDialog@{ trackType ->
                val currentPlayer = styledPlayerView.player ?: return@showTrackDialog

                if (fragmentManager == null || fragmentManager.isStateSaved) return@showTrackDialog

                try {
                    val dialog = when (trackType) {
                        TrackType.AUDIO -> TrackSelectionDialog.createAudioTrackDialogForPlayer(currentPlayer) {}
                        TrackType.CC -> TrackSelectionDialog.createCCDialogForPlayer(currentPlayer) {}
                        TrackType.VIDEO -> TrackSelectionDialog.createForPlayer(currentPlayer) {}
                    }
                    dialog.setStyle(TrackSelectionDialog.STYLE_NORMAL, R.style.TrackSelectionDialogThemeOverlay)
                    dialog.show(fragmentManager, "${trackType.name}_track_dialog")
                } catch (e: Exception) {
                    Log.e("TrackDialog", "Error showing ${trackType.name} dialog", e)
                }
            }

            audioTracksButton.setOnClickListener { showTrackDialog(TrackType.AUDIO) }
            ccTracksButton.setOnClickListener { showTrackDialog(TrackType.CC) }
            videoQualityButton.setOnClickListener { showTrackDialog(TrackType.VIDEO) }

            startOverButton.setOnClickListener { viewModel.triggerTSTVMode(previewTimeBar, forcePosition = 0) }
            jump10sback.setOnClickListener { styledPlayerView.player?.jump10sBack() }
            jump10sforward.setOnClickListener { styledPlayerView.player?.jump10sForward() }

            parentView
        },
        update = { parentView ->
            // --- BLOQUE UPDATE (SE EJECUTA EN RECOMPOSICIONES) ---
            val styledPlayerView = parentView.findViewById<StyledPlayerView>(R.id.player_view)
            val audioTracksButton = parentView.findViewById<AppCompatImageButton>(R.id.audio_tracks_button)
            val ccTracksButton = parentView.findViewById<AppCompatImageButton>(R.id.cc_tracks_button)

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

            styledPlayerView.controllerAutoShow = !showZappingLayer
            viewModel.setDialogButtonVisibility(ccTracksButton, audioTracksButton)

            // --- CORRECCIÓN 2: Ejecutar el FingerprintController solo cuando sea necesario ---
            if (playerChanged || contentChanged) {
                (parentView as? ViewGroup)?.let { viewGroup ->
                    fingerprintController.setup(viewGroup, styledPlayerView)
                    fingerprintController.start(
                        fingerPrintInfo = content?.fingerprintInfo,
                        watermarkInfo = content?.watermarkInfo
                    )
                }
            }
            viewModel.setControlVisibility(styledPlayerView)
        }
    )


}

// Un enum simple para hacer el código de diálogo más limpio
private enum class TrackType { AUDIO, CC, VIDEO }



