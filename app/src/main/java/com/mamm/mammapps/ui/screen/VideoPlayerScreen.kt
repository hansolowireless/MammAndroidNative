package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.uistate.PlayerUIState
import com.mamm.mammapps.ui.theme.SnackbarColor
import com.mamm.mammapps.ui.viewmodel.VideoPlayerViewModel
import kotlinx.coroutines.launch

@Composable
fun VideoPlayerScreen(
    viewModel: VideoPlayerViewModel = hiltViewModel(),
    playedContent: ContentToPlayUI
) {

    val player by viewModel.player.collectAsStateWithLifecycle()
    val content by viewModel.content.collectAsStateWithLifecycle()
    val playerState by viewModel.playerState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.initializeWithContent(content = playedContent)
    }

    LaunchedEffect(content) {
        viewModel.observeLiveEvents()
        viewModel.observeTickers()
        viewModel.updateChannelList()
    }

    LaunchedEffect(playerState) {
        when (val state = playerState) {
            is PlayerUIState.Error -> {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        message = state.message
                    )
                }
            }
            else -> {
            }
        }
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
        PlayerViewWithControlsExperimental(
            modifier = Modifier.fillMaxSize(),
            viewModel = viewModel,
            player = player,
            content = content
        )

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            snackbar = { snackbarData ->
                Snackbar(
                    snackbarData = snackbarData,
                    containerColor = SnackbarColor.containerColor,
                    contentColor = SnackbarColor.contentColor,
                    actionColor = SnackbarColor.actionColor,
                    dismissActionContentColor = SnackbarColor.dismissActionContentColor
                )
            }
        )
    }
}

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

