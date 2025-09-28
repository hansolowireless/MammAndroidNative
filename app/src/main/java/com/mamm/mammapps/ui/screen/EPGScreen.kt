package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.epg.EPGContent
import com.mamm.mammapps.ui.mapper.toContentEntityUI
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.viewmodel.EPGViewModel
import java.time.LocalDate

@Composable
fun EPGScreen(
    viewModel: EPGViewModel = hiltViewModel(),
    onShowDetails: (EPGEvent) -> Unit,
    onPlayClick: (Channel) -> Unit
) {
    val uiState by viewModel.epgUIState.collectAsStateWithLifecycle()
    var selectedDate by remember { mutableStateOf<LocalDate>(LocalDate.now()) }
    val playedChannel by viewModel.playedChannel.collectAsStateWithLifecycle()

    LaunchedEffect (playedChannel) {
        playedChannel?.let{onPlayClick(it)}
        viewModel.clearPlayedChannel()
    }

    LaunchedEffect(Unit) {
        viewModel.getEPGContent(LocalDate.now())
    }

    when (val state = uiState) {
        is UIState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
        is UIState.Success<List<EPGChannelContent>> -> {
            EPGContent(
                epgContent = state.data,
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    selectedDate = date
                    viewModel.getEPGContent(date)
                },
                onEventClicked = { event ->
                    if (event.isLive()) {
                        viewModel.findChannel(event)
                    }
                    else {
                        onShowDetails(event)
                    }
                }
            )
        }
        is UIState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_loading_epg),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        UIState.Idle -> TODO()
    }
}