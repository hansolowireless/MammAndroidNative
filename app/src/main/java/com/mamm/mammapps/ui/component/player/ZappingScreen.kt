package com.mamm.mammapps.ui.component.player

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.constant.UIConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.player.ZappingInfoUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun ZappingScreen(
    modifier: Modifier = Modifier,
    zappingInfo: List<ZappingInfoUI>,
    onChannelClick: (ContentEntityUI) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val focusRequesters = remember(zappingInfo.size) {
        List(zappingInfo.size) { FocusRequester() }
    }

    BackHandler(enabled = true) {
        onDismiss()
    }

    // Este LaunchedEffect ahora se basa en el ÍNDICE del elemento enfocado,
    // que es un valor más estable que el objeto de layout.
    LaunchedEffect(listState.firstVisibleItemIndex) {
        // Establece el tiempo de espera por inactividad (ej. 10 segundos)
        delay(PlayerConstant.MILLISECONDS_SHOW_ZAPPER)
        // Si el 'delay' termina (porque no hubo cambio de foco), llamamos a onDismiss.
        onDismiss()
    }

    LaunchedEffect(focusRequesters) {
        // Solo actuamos si hay requesters para evitar errores
        if (focusRequesters.isNotEmpty()) {
            // Esperamos a que la lista esté lista de forma segura
            snapshotFlow { listState.layoutInfo.visibleItemsInfo }
                .first { it.isNotEmpty() }

            // Un delay corto para asegurar que la composición ha terminado
            delay(100)
            focusRequesters[0].requestFocus()
            listState.animateScrollToItem(0)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)),
        contentAlignment = Alignment.BottomCenter
    ) {
        ProvideLazyListPivotOffset(parentFraction = 0.80f) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(horizontal = 48.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                reverseLayout = true
            ) {
                itemsIndexed(
                    items = zappingInfo,
                    key = { _, info -> "${info.channel.identifier.id}_${info.channel.title}" }
                ) { index, zappingInfoItem ->
                    ContentEntityListItem(
                        modifier = Modifier.focusRequester(focusRequesters[index]),
                        channelInfo = zappingInfoItem.channel,
                        content = zappingInfoItem.liveEvent,
                        showLiveIndicator = false,
                        showCatchupIndicator = false,
                        onClick = {
                            onChannelClick(zappingInfoItem.channel)
                        }
                    )
                }
            }
        }
    }
}