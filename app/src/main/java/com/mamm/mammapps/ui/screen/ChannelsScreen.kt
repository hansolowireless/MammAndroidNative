package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.ui.component.HomeGridTop
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.MammAppsTheme
import com.mamm.mammapps.ui.viewmodel.ChannelsViewModel

@Composable
fun ChannelsScreen(
    modifier: Modifier = Modifier,
    viewModel: ChannelsViewModel = hiltViewModel(),
    onContentClicked: (Channel) -> Unit = {}
) {

    val focusedContent by viewModel.focusedContent.collectAsStateWithLifecycle()
    val liveEvent by viewModel.liveEventInfo.collectAsStateWithLifecycle()
    val channels by viewModel.channels.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    LaunchedEffect (Unit) {
        viewModel.getChannels()
    }

    LaunchedEffect(focusedContent) {
        viewModel.observeLiveEvents()
    }

    LaunchedEffect(channels) {
        viewModel.setFirstFocusedContent()
    }

    LaunchedEffect(clickedContent) {
        clickedContent?.let { content ->
            if (!hasNavigated.value) {
                onContentClicked(content)
                hasNavigated.value = true
                viewModel.clearClickedContent()
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        liveEvent?.let {
            HomeGridTop(event = it)
        }

        ChannelGrid(
            onChannelClick = viewModel::findChannel,
            channels = channels,
            onChannelFocus = viewModel::setFocusedContent
        )
    }
}

@Composable
fun ChannelGrid(
    modifier: Modifier = Modifier,
    channels: List<ContentEntityUI>,
    onChannelClick: (ContentEntityUI) -> Unit,
    onChannelFocus: (ContentEntityUI) -> Unit = {}
) {
    ProvideLazyListPivotOffset (parentFraction = 0.15f) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            items(channels) { channel ->
                ContentEntity(
                    contentEntityUI = channel,
                    onClick = { onChannelClick(channel) },
                    onFocus = { onChannelFocus(channel) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChannelsScreenPreview() {
    MammAppsTheme {
        ChannelsScreen()
    }
}