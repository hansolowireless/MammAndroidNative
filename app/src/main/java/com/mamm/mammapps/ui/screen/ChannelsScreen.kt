package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.channels.ChannelFilter
import com.mamm.mammapps.ui.component.channels.ChannelGridMobile
import com.mamm.mammapps.ui.component.channels.ChannelGridTV
import com.mamm.mammapps.ui.component.home.HomeGridTop
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
    val channels by viewModel.filteredChannels.collectAsStateWithLifecycle()
    val channelGenres by viewModel.channelGenres.collectAsStateWithLifecycle()
    val selectedGenres by viewModel.selectedGenres.collectAsStateWithLifecycle()
    val clickedContent by viewModel.clickedContent.collectAsStateWithLifecycle()
    val hasNavigated = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
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

    if (LocalIsTV.current) {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            liveEvent?.let {
                HomeGridTop(content = it)
            }

            ChannelFilter(
                availableGenres = channelGenres,
                selectedGenres = selectedGenres,
                onSelectedGenresChanged = {
                    viewModel.filterChannelsByGenres(it)
                },
                onSearchQueryChanged = {
                    viewModel.filterChannelsByQuery(it)
                },
                onClearSearch = {
                    viewModel.resetSelectedGenres()
                }
            )

            ChannelGridTV(
                onChannelClick = viewModel::findChannel,
                channels = channels,
                onChannelFocus = viewModel::setFocusedContent
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChannelFilter(
                availableGenres = channelGenres,
                selectedGenres = selectedGenres,
                onSelectedGenresChanged = {
                    viewModel.filterChannelsByGenres(it)
                },
                onSearchQueryChanged = {
                    viewModel.filterChannelsByQuery(it)
                },
                onClearSearch = {
                    viewModel.resetSelectedGenres()
                }
            )

            ChannelGridMobile(
                onChannelClick = viewModel::findChannel,
                channels = channels,
                onChannelFocus = viewModel::setFocusedContent
            )
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