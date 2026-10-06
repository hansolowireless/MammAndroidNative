package com.mamm.mammapps.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.epg.DateSelector
import com.mamm.mammapps.ui.component.epg.EPGMobile
import com.mamm.mammapps.ui.component.epg.EPGTV
import com.mamm.mammapps.ui.mapper.toContentToPlayUI
import com.mamm.mammapps.ui.model.uistate.CastState
import com.mamm.mammapps.ui.model.uistate.UIState
import com.mamm.mammapps.ui.viewmodel.CastViewModel
import com.mamm.mammapps.ui.viewmodel.EPGViewModel

@Composable
fun EPGScreen(
    viewModel: EPGViewModel = hiltViewModel(),
    castViewModel: CastViewModel = hiltViewModel(),
    onShowDetails: (Event) -> Unit,
    onPlayClick: (Channel) -> Unit
) {

    val isTV = LocalIsTV.current
    val castState by castViewModel.castState.collectAsStateWithLifecycle()

    val uiState by viewModel.epgUIState.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val selectedChannel by viewModel.selectedChannel.collectAsStateWithLifecycle()
    val playedChannel by viewModel.playedChannel.collectAsStateWithLifecycle()

    // El día seleccionado vive en el ViewModel, que sobrevive a ir al detalle y volver.
    // Si aquí se pidiera LocalDate.now() se recargaría hoy mientras el selector sigue
    // marcando otro día, y la parrilla saldría colocada contra el día equivocado.
    LaunchedEffect(Unit) {
        viewModel.getEPGContent(selectedDate)
    }

    LaunchedEffect(Unit) {
        if (!isTV) {
            castViewModel.startChromecast()
        }
    }

    LaunchedEffect(playedChannel) {
        playedChannel?.let {
            when (castState) {
                is CastState.SessionStarted -> {
                    castViewModel.loadRemoteMedia(it.toContentToPlayUI())
                }
                else -> {
                    onPlayClick(it)
                }
            }
            viewModel.clearPlayedChannel()
        }
    }

    Column {

        // Fuera del when a propósito: el selector tiene que seguir en pantalla mientras
        // se carga el día nuevo, en vez de desaparecer con el spinner. En TV no, porque
        // allí es una de las tres columnas de la propia rejilla.
        if (!isTV) {
            DateSelector(
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    viewModel.setSelectedDate(date)
                    viewModel.getEPGContent(date)
                }
            )
        }

        when (val state = uiState) {
            is UIState.Loading -> {
                LoadingSpinner(modifier = Modifier.fillMaxSize())
            }

            is UIState.Success<List<EPGChannelContent>> -> {
                if (isTV) {
                    EPGTV(
                        epgContent = state.data,
                        selectedDate = selectedDate,
                        onDateSelected = { date ->
                            viewModel.setSelectedDate(date)
                            viewModel.getEPGContent(date)
                        },
                        selectedChannel = selectedChannel,
                        onChannelSelected = { channel ->
                            viewModel.setSelectedChannel(channel)
                        },
                        onEventClicked = { event ->
                            if (event.isLive()) {
                                viewModel.findChannel(event)
                            } else {
                                onShowDetails(event)
                            }
                        }
                    )
                } else {
                    EPGMobile(
                        content = state.data,
                        selectedDate = selectedDate,
                        onEventClicked = {
                            if (it.isLive()) {
                                viewModel.findChannel(it)
                            } else {
                                onShowDetails(it)
                            }
                        }
                    )
                }
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
}