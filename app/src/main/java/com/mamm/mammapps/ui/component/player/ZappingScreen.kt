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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
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
import com.mamm.mammapps.ui.component.common.LoadingSpinner
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import com.mamm.mammapps.ui.model.player.ZappingInfoUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
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

    // La lista puede llegar después de abrir la capa (mientras se ve el spinner)
    val hasData = zappingInfo.isNotEmpty()
    val openedEmpty = remember { zappingInfo.isEmpty() }

    // La cuenta atrás sobrevive a las recomposiciones: tiene que usar los valores más recientes
    val latestZappingInfo by rememberUpdatedState(zappingInfo)
    val latestCurrentChannel by rememberUpdatedState(currentChannel)
    val latestOnChannelClick by rememberUpdatedState(onChannelClick)
    val latestOnDismiss by rememberUpdatedState(onDismiss)

    // Con hasData en la clave, la cuenta atrás vuelve a empezar al llegar la lista. El índice
    // visible se observa aquí dentro para no recomponer la pantalla en cada scroll.
    LaunchedEffect(hasData) {
        snapshotFlow { listState.firstVisibleItemIndex }.collectLatest { index ->
            delay(PlayerConstant.MILLISECONDS_SHOW_ZAPPER)
            latestZappingInfo.getOrNull(index)?.let { focusedItem ->
                // Si el canal enfocado es distinto al actual, disparamos el click
                if (focusedItem.channel.identifier.id != latestCurrentChannel?.identifier?.id) {
                    latestOnChannelClick(focusedItem.channel)
                }
            }
            latestOnDismiss()
        }
    }

    LaunchedEffect(hasData) {
        if (!hasData) return@LaunchedEffect

        // El índice inicial del listState solo se aplica en la primera composición: si entonces
        // la lista estaba vacía, hay que llevarla al canal actual a mano.
        if (openedEmpty) listState.scrollToItem(initialIndex)

        // Esperamos a que el elemento esté compuesto: pedir foco antes lanzaría una excepción
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.any { it.index == initialIndex } }
            .first { it }

        // Pedimos el foco.
        focusRequesters.getOrNull(initialIndex)?.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f))
            .onInterceptKeyBeforeSoftKeyboard {
                if (it.key == Key.Back ||
                    it.key == Key.DirectionLeft ||
                    it.key == Key.DirectionRight
                ) {
                    onDismiss()
                    true
                } else {
                    false
                }
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        // La lista puede tardar en llegar si hay que descargar la EPG para buscar los directos
        if (zappingInfo.isEmpty()) {
            LoadingSpinner(modifier = Modifier.align(Alignment.Center))
        } else {
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
                            leadingContent = zappingInfoItem.channel,
                            mainContent = zappingInfoItem.liveEvent,
                            showFullTitle = true,
                            showDescription = true,
                            descriptionMaxLines = 2,
                            //Se tiene que ver que en la posición 0 el canal tiene el número 1
                            orderIndex = index + 1,
                            onClick = {
                                onChannelClick(zappingInfoItem.channel)
                            }
                        )
                    }
                }
            }
        }
    }
}
