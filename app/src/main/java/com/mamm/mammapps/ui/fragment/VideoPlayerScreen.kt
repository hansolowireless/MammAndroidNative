package com.mamm.mammapps.ui.fragment

import android.widget.ImageView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.mamm.mammapps.data.model.player.VideoPlayerUIState
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.model.ContentIdentifier
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.viewmodel.PlayerViewModel
import com.mamm.mammapps.ui.viewmodel.VideoPlayerEvent
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    playedContent: ContentToPlayUI
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val player by viewModel.player.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()

    LaunchedEffect (Unit) {
        viewModel.initializeWithContent(content = playedContent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VideoPlayerView(
            player = player,
            isRadio = content?.isRadio,
            logoURL = content?.imageUrl,
            modifier = Modifier.fillMaxSize()
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
fun VideoPlayerView(
    modifier: Modifier = Modifier,
    player: ExoPlayer?,
    isRadio: Boolean? = false,
    logoURL: String?,
) {
    AndroidView(
        factory = { context ->
            PlayerView(context).apply {
                useController = false
                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                this.player = player

                if (isRadio == true && !logoURL.isNullOrEmpty()) {
                    Glide.with(context)
                        .load(logoURL)
                        .into(findViewById<ImageView>(com.google.android.exoplayer2.ui.R.id.exo_artwork))
                }
            }
        },
        update = { playerView ->
            playerView.player = player
        },
        modifier = modifier
    )
}

//@Composable
//fun PlayerControlsOverlay(
//    uiState: VideoPlayerUIState,
//    content: ContentToPlayUI?,
//    onEvent: (VideoPlayerEvent) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var controlsVisible by remember { mutableStateOf(false) }
//
//    LaunchedEffect(uiState.showControls) {
//        controlsVisible = uiState.showControls
//        if (controlsVisible) {
//            delay(5000)
//            controlsVisible = false
//        }
//    }
//
//    Box(modifier = modifier) {
//        Surface(
//            modifier = Modifier
//                .fillMaxSize()
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = null
//                ) {
//                    controlsVisible = !controlsVisible
//                    onEvent(VideoPlayerEvent.ToggleControls)
//                },
//            color = Color.Transparent
//        ) {}
//
//        AnimatedVisibility(
//            visible = controlsVisible && !uiState.isZapping,
//            enter = fadeIn(),
//            exit = fadeOut(),
//            modifier = Modifier.fillMaxSize()
//        ) {
//            PlayerControls(
//                uiState = uiState,
//                content = content,
//                onEvent = onEvent
//            )
//        }
//
//        ContentInfo(
//            eventChannelName = uiState.eventChannelName,
//            eventTitle = uiState.eventTitle,
//            logoURL = uiState.logoURL,
//            modifier = Modifier
//                .align(Alignment.TopStart)
//                .padding(Dimensions.paddingMedium)
//        )
//
//        if (!LocalIsTV.current) {
//            IconButton(
//                onClick = { onEvent(VideoPlayerEvent.Close) },
//                modifier = Modifier
//                    .align(Alignment.TopEnd)
//                    .padding(Dimensions.paddingMedium)
//            ) {
//                Icon(
//                    Icons.Default.Close,
//                    contentDescription = "Cerrar",
//                    tint = Color.White
//                )
//            }
//        }
//
//        if (uiState.showLiveIndicator) {
//            LiveIndicator(
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(Dimensions.paddingMedium)
//            )
//        }
//    }
//}
//
//@Composable
//fun PlayerControls(
//    uiState: VideoPlayerUIState,
//    content: ContentToPlayUI?,
//    onEvent: (VideoPlayerEvent) -> Unit
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                Brush.verticalGradient(
//                    colors = listOf(
//                        Color.Black.copy(alpha = 0.7f),
//                        Color.Transparent,
//                        Color.Black.copy(alpha = 0.7f)
//                    )
//                )
//            )
//    ) {
//        TopControls(
//            eventChannelName = uiState.eventChannelName,
//            eventTitle = uiState.eventTitle,
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .fillMaxWidth()
//                .padding(Dimensions.paddingMedium)
//        )
//
//        CenterControls(
//            content = content,
//            uiState = uiState,
//            onEvent = onEvent,
//            modifier = Modifier.align(Alignment.Center)
//        )
//
//        BottomControls(
//            uiState = uiState,
//            content = content,
//            onEvent = onEvent,
//            modifier = Modifier
//                .align(Alignment.BottomCenter)
//                .fillMaxWidth()
//                .padding(Dimensions.paddingMedium)
//        )
//    }
//}
//
//@Composable
//fun CenterControls(
//    content: ContentToPlayUI?,
//    uiState: VideoPlayerUIState,
//    onEvent: (VideoPlayerEvent) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Row(
//        modifier = modifier,
//        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingLarge),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        if (content.identifier !is ContentIdentifier.Channel) {
//            IconButton(
//                onClick = { onEvent(VideoPlayerEvent.Rewind) }
//            ) {
//                Icon(
//                    Icons.Default.Replay10,
//                    contentDescription = "Retroceder",
//                    tint = Color.White,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//        }
//
//        IconButton(
//            onClick = { onEvent(VideoPlayerEvent.TogglePlayPause) }
//        ) {
//            Icon(
//                if (uiState.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
//                contentDescription = if (uiState.isPlaying) "Pausar" else "Reproducir",
//                tint = Color.White,
//                modifier = Modifier.size(64.dp)
//            )
//        }
//
//        if (content.identifier !is ContentIdentifier.Channel ) {
//            IconButton(
//                onClick = { onEvent(VideoPlayerEvent.FastForward) }
//            ) {
//                Icon(
//                    Icons.Default.Forward10,
//                    contentDescription = "Avanzar",
//                    tint = Color.White,
//                    modifier = Modifier.size(48.dp)
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun BottomControls(
//    uiState: VideoPlayerUIState,
//    content: ContentToPlayUI?,
//    onEvent: (VideoPlayerEvent) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier) {
//        if (!content.isLive || uiState.isTimeshift) {
//            CustomProgressBar(
//                uiState = uiState,
//                onEvent = onEvent,
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
//        }
//
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            LeftControls(onEvent = onEvent)
//
//            CenterTimeDisplay(uiState = uiState)
//
//            RightControls(uiState = uiState, onEvent = onEvent)
//        }
//    }
//}
//
//@Composable
//fun LeftControls(onEvent: (VideoPlayerEvent) -> Unit) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
//    ) {
//        IconButton(onClick = { onEvent(VideoPlayerEvent.ShowSubtitles) }) {
//            Icon(
//                Icons.Default.Subtitles,
//                contentDescription = "Subtítulos",
//                tint = Color.White
//            )
//        }
//
//        IconButton(onClick = { onEvent(VideoPlayerEvent.ShowAudioTracks) }) {
//            Icon(
//                Icons.Default.AudioFile,
//                contentDescription = "Audio",
//                tint = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun CenterTimeDisplay(content: ContentToPlayUI, currentPosition: Long = 0, duration: Long = 0) {
//    when {
//        (!content.isLive) -> {
//            Row {
//                Text(
//                    text = formatTime(currentPosition),
//                    color = Color.White,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Text(
//                    text = " / ",
//                    color = Color.White,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//                Text(
//                    text = formatTime(duration),
//                    color = Color.White,
//                    style = MaterialTheme.typography.bodyMedium
//                )
//            }
//        }
//        content.isTimeshift -> {
//            Text(
//                text = uiState.tstvHourBeginText,
//                color = Color.White,
//                style = MaterialTheme.typography.bodyMedium
//            )
//        }
//    }
//}
//
//@Composable
//fun RightControls(
//    uiState: VideoPlayerUIState,
//    content: ContentToPlayUI,
//    onEvent: (VideoPlayerEvent) -> Unit
//) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
//    ) {
//        if (content.isLive && uiState.isTimeshift) {
//            IconButton(onClick = { onEvent(VideoPlayerEvent.GoToBeginning) }) {
//                Icon(
//                    Icons.Default.SkipPrevious,
//                    contentDescription = "Ir al inicio",
//                    tint = Color.White
//                )
//            }
//
//            if (!uiState.showLiveIndicator) {
//                IconButton(onClick = { onEvent(VideoPlayerEvent.GoToLive) }) {
//                    Icon(
//                        Icons.Default.PlayArrow,
//                        contentDescription = "Ir a directo",
//                        tint = Color.Red
//                    )
//                }
//            }
//        }
//
//        IconButton(onClick = { onEvent(VideoPlayerEvent.ShowSettings) }) {
//            Icon(
//                Icons.Default.Settings,
//                contentDescription = "Configuración",
//                tint = Color.White
//            )
//        }
//    }
//}
//
//@Composable
//fun CustomProgressBar(
//    uiState: VideoPlayerUIState,
//    onEvent: (VideoPlayerEvent) -> Unit,
//    modifier: Modifier = Modifier
//) {
//    var isDragging by remember { mutableStateOf(false) }
//    var dragPosition by remember { mutableStateOf(0f) }
//
//    val progress = if (isDragging) {
//        dragPosition
//    } else {
//        if (uiState.duration > 0) {
//            uiState.currentPosition.toFloat() / uiState.duration.toFloat()
//        } else 0f
//    }
//
//    Slider(
//        value = progress,
//        onValueChange = { newValue ->
//            isDragging = true
//            dragPosition = newValue
//            onEvent(VideoPlayerEvent.SeekTo((newValue * uiState.duration).toLong()))
//        },
//        onValueChangeFinished = {
//            isDragging = false
//            onEvent(VideoPlayerEvent.SeekFinished)
//        },
//        colors = SliderDefaults.colors(
//            thumbColor = Color.Red,
//            activeTrackColor = Color.Red,
//            inactiveTrackColor = Color.White.copy(alpha = 0.3f)
//        ),
//        modifier = modifier
//    )
//}
//
//@Composable
//fun TopControls(
//    eventChannelName: String?,
//    eventTitle: String?,
//    modifier: Modifier = Modifier
//) {
//    Column(modifier = modifier) {
//        eventChannelName?.let { channelName ->
//            Text(
//                text = channelName,
//                color = Color.White,
//                style = MaterialTheme.typography.headlineSmall,
//                fontWeight = FontWeight.Bold
//            )
//        }
//
//        eventTitle?.let { title ->
//            Text(
//                text = title,
//                color = Color.White.copy(alpha = 0.8f),
//                style = MaterialTheme.typography.bodyLarge
//            )
//        }
//    }
//}
//
//@Composable
//fun ContentInfo(
//    eventChannelName: String?,
//    eventTitle: String?,
//    logoURL: String?,
//    modifier: Modifier = Modifier
//) {
//    if (eventChannelName != null || eventTitle != null) {
//        Row(
//            modifier = modifier
//                .background(
//                    Color.Black.copy(alpha = 0.6f),
//                    RoundedCornerShape(Dimensions.cornerRadius)
//                )
//                .padding(Dimensions.paddingSmall),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            logoURL?.let { logo ->
//                AsyncImage(
//                    model = logo,
//                    contentDescription = "Logo del canal",
//                    modifier = Modifier
//                        .size(40.dp)
//                        .clip(RoundedCornerShape(4.dp))
//                )
//
//                Spacer(modifier = Modifier.width(Dimensions.paddingSmall))
//            }
//
//            Column {
//                eventChannelName?.let { channelName ->
//                    Text(
//                        text = channelName,
//                        color = Color.White,
//                        style = MaterialTheme.typography.titleMedium,
//                        fontWeight = FontWeight.Bold
//                    )
//                }
//
//                eventTitle?.let { title ->
//                    Text(
//                        text = title,
//                        color = Color.White.copy(alpha = 0.8f),
//                        style = MaterialTheme.typography.bodyMedium
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun LiveIndicator(
//    modifier: Modifier = Modifier
//) {
//    Box(
//        modifier = modifier
//            .background(
//                Color.Red,
//                RoundedCornerShape(Dimensions.paddingMedium)
//            )
//            .padding(horizontal = Dimensions.paddingSmall, vertical = 6.dp)
//    ) {
//        Text(
//            text = "EN VIVO",
//            color = Color.White,
//            style = MaterialTheme.typography.labelMedium,
//            fontWeight = FontWeight.Bold
//        )
//    }
//}
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

fun formatTime(timeMs: Long): String {
    val totalSeconds = timeMs / 1000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%d:%02d", minutes, seconds)
    }
}
