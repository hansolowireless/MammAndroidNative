package com.mamm.mammapps.ui.component.player

import androidx.compose.foundation.background
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onInterceptKeyBeforeSoftKeyboard
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.ZappingInfoUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first

@Composable
fun ZappingScreen(
    modifier: Modifier = Modifier,
    zappingInfo: List<ZappingInfoUI>,
    currentChannel: ContentToPlayUI?,
    onChannelClick: (ContentEntityUI) -> Unit,
    onDismiss: () -> Unit
) {
    val initialIndex = remember(zappingInfo, currentChannel) {
        zappingInfo.indexOfFirst {
            it.channel.identifier.id == currentChannel?.identifier?.id
        }.coerceAtLeast(0)
    }

    val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)

    val focusRequesters = remember(zappingInfo.size) {
        List(zappingInfo.size) { FocusRequester() }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        delay(PlayerConstant.MILLISECONDS_SHOW_ZAPPER)
        onDismiss()
    }

    // 4. ELIMINAMOS EL SCROLL Y SOLO DAMOS EL FOCO
    LaunchedEffect(Unit) { // Se ejecuta solo una vez
        if (focusRequesters.isNotEmpty()) {
            // Esperamos a que la lista se haya compuesto en su estado inicial
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.isNotEmpty() }
                .first { it }

            // La lista ya está en la posición correcta, solo pedimos el foco.
            focusRequesters[initialIndex].requestFocus()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .onInterceptKeyBeforeSoftKeyboard {
                if (it.key == Key.Back) {
                    onDismiss()
                    true
                } else {
                    false
                }
            },
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
                        modifier = Modifier
                            .focusRequester(focusRequesters[index]),
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
