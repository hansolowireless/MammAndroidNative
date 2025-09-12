package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.ui.common.UIState
import com.mamm.mammapps.ui.component.epg.EPGContent
import com.mamm.mammapps.ui.viewmodel.EPGViewModel

@Composable
fun EPGScreen(
    viewModel: EPGViewModel = hiltViewModel()
) {
    val uiState by viewModel.epgUIState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.getEPGContent()
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
            EPGContent(epgContent = state.data)
        }
        is UIState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading EPG data",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        UIState.Idle -> TODO()
    }
}