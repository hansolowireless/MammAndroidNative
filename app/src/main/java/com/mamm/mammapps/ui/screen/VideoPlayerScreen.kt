package com.mamm.mammapps.ui.screen

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.openstream_flutter_rw.ui.manager.watermark.FingerprintController
import com.github.rubensousa.previewseekbar.PreviewBar
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.Ticker
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.component.player.dialogs.TrackSelectionDialog
import com.mamm.mammapps.ui.manager.videoresize.VideoResizeManagerWithTicker
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.viewmodel.VideoPlayerViewModel

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    playedContent: ContentToPlayUI
) {

    val player by viewModel.player.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.initializeWithContent(content = playedContent)
    }

    LaunchedEffect(content) {
        viewModel.observeLiveEvents()
        viewModel.observeTickers()
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.releaseVariables()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {

        PlayerViewWithControls(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            player = player
        )

    }
}

@Composable
fun PlayerViewWithControls(
    modifier: Modifier = Modifier,
    viewModel: VideoPlayerViewModel,
    player: ExoPlayer?,
    content: ContentToPlayUI? = null
) {
    val focusRequester = remember { FocusRequester() }
    var playerViewRef by remember { mutableStateOf<StyledPlayerView?>(null) }

    //Watermark y fingerprinting
    val fingerprintController = remember { FingerprintController() }

    //Tickers
    var videoResizeManager by remember { mutableStateOf<VideoResizeManagerWithTicker?>(null) }
    val tickerList by viewModel.tickerList.collectAsStateWithLifecycle()

    //Barra para TSTV
    var previewTimeBar by remember { mutableStateOf<CustomPreviewBar?>(null) }
    var scrubListener by remember { mutableStateOf<PreviewBar.OnScrubListener?>(null) }
    val liveEventInfo by viewModel.liveEventInfo.collectAsStateWithLifecycle()
    val isTstvMode by viewModel.isTstvMode.collectAsStateWithLifecycle()

    val lifecycleOwner = LocalLifecycleOwner.current

    // Mostrar diálogo de subtítulos
    val context = LocalContext.current
    val fragmentManager = (context as? FragmentActivity)?.supportFragmentManager
    val showCcDialog by viewModel.showCcDialog.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    LaunchedEffect(liveEventInfo, content, isTstvMode) {
        playerViewRef?.let { playerView -> viewModel.setControlVisibility(playerView)  }
    }

    LaunchedEffect (tickerList) {
        videoResizeManager?.replaceTickers(tickerList)
    }

    LaunchedEffect(showCcDialog) {
        if (showCcDialog && fragmentManager != null) {
            val trackSelectionDialog = TrackSelectionDialog.createCCDialogForPlayer(
                player
            ) { dismissedDialog ->  // Este es el onDismissListener
                viewModel.onCcDialogDismissed()
            }
            trackSelectionDialog.show(fragmentManager, null)
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
            previewTimeBar = parentView.findViewById<CustomPreviewBar>(R.id.exo_progress)

            val ccTracksButton = parentView.findViewById<AppCompatImageButton>(R.id.cc_tracks_button)
            ccTracksButton?.setOnClickListener {
                viewModel.onCcDialogButtonClick()
            }

            // Inicializar manager si no existe
            if (videoResizeManager == null) {
                val dummyFragment = object : Fragment() {
                    override fun getView(): View = parentView
                    override fun getContext(): Context? = context
                    override fun getViewLifecycleOwner(): LifecycleOwner = lifecycleOwner
                }

                videoResizeManager = VideoResizeManagerWithTicker(
                    fragment = dummyFragment,
                    frameLayoutId = R.id.root,
                    tickerList = emptyList()
                )
            }

            styledPlayerView?.let { playerView ->
                playerView.player = player
                playerView.useController = true
                playerView.controllerShowTimeoutMs = 10000

                // Solo agregar listener si no existe
                if (scrubListener == null) {
                    val newListener = object : PreviewBar.OnScrubListener {
                        override fun onScrubStart(previewBar: PreviewBar?) {
                        }

                        override fun onScrubMove(
                            previewBar: PreviewBar?,
                            progress: Int,
                            fromUser: Boolean
                        ) {
                        }

                        override fun onScrubStop(previewBar: PreviewBar?) {
                            viewModel.handleScrubStop(previewTimeBar)
                        }
                    }

                    previewTimeBar?.addOnScrubListener(newListener)
                    scrubListener = newListener
                }


                fingerprintController.setup(parentView, styledPlayerView)
                fingerprintController.start(
                    fingerPrintInfo = content?.fingerprintInfo,
                    watermarkInfo = content?.watermarkInfo
                )
            }
        },
        modifier = modifier
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {

                    val isControllerVisible = playerViewRef?.isControllerFullyVisible ?: false

                    when (keyEvent.key) {
                        Key.DirectionCenter,
                        Key.Enter,
                        Key.DirectionUp,
                        Key.DirectionDown,
                        Key.DirectionLeft,
                        Key.DirectionRight -> {
                            Log.d(
                                "ComposeKey",
                                "D-pad pressed, controller visible: $isControllerVisible"
                            )
                            if (!isControllerVisible) {
                                // Mostrar controles si no están visibles
                                playerViewRef?.showController()
                                true // Consumir evento
                            } else {
                                // Permitir navegación por los controles
                                false // No consumir, dejar pasar a ExoPlayer
                            }
                        }

                        else -> false
                    }
                } else false
            }
    )

}

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

