package com.mamm.mammapps.ui.screen

import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.Glide
import com.example.openstream_flutter_rw.ui.manager.watermark.FingerprintController
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.WatermarkInfo
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.viewmodel.PlayerViewModel

@Composable
fun VideoPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    playedContent: ContentToPlayUI
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeWithContent(content = playedContent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        PlayerViewWithControls(
            modifier = Modifier.fillMaxSize(),
            player = player
        )

//        PlayerControlsOverlay(
//            uiState = uiState,
//            content = content,
//            onEvent = viewModel::handleEvent,
//            modifier = Modifier.fillMaxSize()
//        )
//
//        PlayerDialogs(
//            uiState = uiState,
//            onEvent = viewModel::handleEvent
//        )
//
//        if (uiState.showChannelZapDisplay) {
//            ChannelZapDisplay(
//                channelNumber = uiState.channelZapNumber,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        }
//
//        uiState.error?.let { error ->
//            ErrorDisplay(
//                message = error,
//                onDismiss = { viewModel.clearError() },
//                modifier = Modifier.align(Alignment.BottomCenter)
//            )
//        }
    }
}

@Composable
fun PlayerViewWithControls(
    modifier: Modifier = Modifier,
    player: ExoPlayer?
) {
    val watermarkController = remember { FingerprintController() }
    val focusRequester = remember { FocusRequester() }
    var playerViewRef by remember { mutableStateOf<StyledPlayerView?>(null) }

    DisposableEffect(Unit) {
        onDispose {
            watermarkController.stop()
        }
    }

    AndroidView(
        factory = { context ->
            val parentView = FrameLayout(context)
            LayoutInflater.from(context).inflate(
                R.layout.activity_video_player_thumbnail_mobile,
                parentView,
                true
            )
            parentView
        },
        update = { parentView ->
            val styledPlayerView = parentView.findViewById<StyledPlayerView>(R.id.player_view)
            playerViewRef = styledPlayerView

            styledPlayerView?.let { playerView ->
                playerView.player = player
                playerView.useController = true
                playerView.controllerShowTimeoutMs = 10000

                player?.let { p ->
                    Log.d("PlayerDebug", "Player state: ${p.playbackState}")
                    Log.d("PlayerDebug", "Has media items: ${p.mediaItemCount}")
                    Log.d("PlayerDebug", "Duration: ${p.duration}")
                    Log.d("PlayerDebug", "Position: ${p.currentPosition}")
                }

                watermarkController.setup(parentView, styledPlayerView, "Default Text")
                watermarkController.start(
                    enabled = true,
                    interval = 30,
                    duration = 5,
                    position = "random",
                    text = "Watermark",
                    watermarkInfo = WatermarkInfo(has = true)
                )
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                Log.d("ComposeKey", "Key: ${keyEvent.key}, Type: ${keyEvent.type}")

                if (keyEvent.type == KeyEventType.KeyDown) {
                    val playerView = playerViewRef
                    val isControllerVisible = playerView?.isControllerFullyVisible ?: false

                    when (keyEvent.key) {
                        Key.DirectionCenter,
                        Key.Enter -> {
                            Log.d("ComposeKey", "Center/Enter pressed, controller visible: $isControllerVisible")
                            if (!isControllerVisible) {
                                playerView?.showController()
                                true
                            } else {
                                // Dejar que ExoPlayer maneje la navegación
                                false
                            }
                        }
                        Key.DirectionUp,
                        Key.DirectionDown,
                        Key.DirectionLeft,
                        Key.DirectionRight -> {
                            Log.d("ComposeKey", "D-pad navigation, controller visible: $isControllerVisible")
                            if (!isControllerVisible) {
                                // Mostrar controles si no están visibles
                                playerView?.showController()
                                true
                            } else {
                                // Permitir navegación por los controles
                                false
                            }
                        }
                        else -> false
                    }
                } else false
            }
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}

//
//@Composable
//fun PlayerDialogs(
//    uiState: VideoPlayerUIState,
//    onEvent: (VideoPlayerEvent) -> Unit
//) {
//    if (uiState.showPINDialog) {
//        PINDialog(
//            onPINEntered = { pin -> onEvent(VideoPlayerEvent.ValidatePIN(pin)) },
//            onDismiss = { onEvent(VideoPlayerEvent.HidePINDialog) }
//        )
//    }
//}
//
//@Composable
//fun PINDialog(
//    onPINEntered: (String) -> Unit,
//    onDismiss: () -> Unit
//) {
//    var pin by remember { mutableStateOf("") }
//
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Introduce PIN") },
//        text = {
//            OutlinedTextField(
//                value = pin,
//                onValueChange = { pin = it },
//                label = { Text("PIN") },
//                visualTransformation = PasswordVisualTransformation(),
//                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
//            )
//        },
//        confirmButton = {
//            TextButton(
//                onClick = {
//                    onPINEntered(pin)
//                    pin = ""
//                }
//            ) {
//                Text("OK")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) {
//                Text("Cancelar")
//            }
//        }
//    )
//}
//
//@Composable
//fun ChannelZapDisplay(
//    channelNumber: String,
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .background(
//                Color.Black.copy(alpha = 0.8f),
//                RoundedCornerShape(Dimensions.cornerRadius)
//            )
//            .padding(Dimensions.paddingLarge)
//    ) {
//        Text(
//            text = channelNumber,
//            color = Color.White,
//            style = MaterialTheme.typography.displayLarge,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}
//
//@Composable
//fun ErrorDisplay(
//    message: String,
//    onDismiss: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Card(
//        modifier = modifier
//            .padding(Dimensions.paddingMedium)
//            .clickable { onDismiss() },
//        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.9f))
//    ) {
//        Text(
//            text = message,
//            color = Color.White,
//            style = MaterialTheme.typography.bodyMedium,
//            modifier = Modifier.padding(Dimensions.paddingMedium)
//        )
//    }
//}

