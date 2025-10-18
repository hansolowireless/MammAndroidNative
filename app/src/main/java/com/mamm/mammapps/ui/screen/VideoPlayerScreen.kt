package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
        viewModel.updateChannelList()
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

