package com.mamm.mammapps.ui.component.channels

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.contententity.ContentEntity
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions
import kotlinx.coroutines.delay

@Composable
fun ChannelGridTV(
    modifier: Modifier = Modifier,
    channels: List<ContentEntityUI>,
    onChannelClick: (ContentEntityUI) -> Unit,
    onChannelFocus: (ContentEntityUI) -> Unit = {}
) {
    val lazyGridState = rememberLazyGridState()

    //  Guardamos el índice del último elemento que tuvo el foco.
    // 'rememberSaveable' es clave para que sobreviva a cambios de configuración y al volver a la pantalla.
    val lastFocusedIndex = rememberSaveable { mutableIntStateOf(0) }
    val focusRequester = remember { FocusRequester() }

    val channelsKey by remember(channels) {
        derivedStateOf {
            channels.joinToString { it.identifier.id.toString() }
        }
    }

    val previousChannelsKey = rememberSaveable { mutableStateOf(channelsKey) }

    LaunchedEffect(Unit) {
        delay(50)
        focusRequester.requestFocus()
    }

    LaunchedEffect(channelsKey) {
        if (previousChannelsKey.value != channelsKey) {
            //Hace scroll al 0 al cambiar de categoría de canales
            lastFocusedIndex.intValue = 0
            lazyGridState.scrollToItem(0)
            previousChannelsKey.value = channelsKey
        } else {
            /*Para que al navegar a la sección
            el foco se vaya al primer canal
            */
            delay(50)
            focusRequester.requestFocus()
        }
    }

    ProvideLazyListPivotOffset(parentFraction = 0.1f) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            state = lazyGridState,
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = Dimensions.paddingMedium,
                vertical = Dimensions.paddingSmall
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingMedium),
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            itemsIndexed(
                channels,
                key = { _, channel -> channel.identifier.id }) { index, channel ->
                ContentEntity(
                    modifier = Modifier
                        // El FocusRequester se asigna dinámicamente al último elemento enfocado.
                        // Cuando volvemos a la pantalla, lastFocusedIndex tiene el valor guardado
                        // y el focusRequester se asigna al item correcto ANTES de que el LaunchedEffect lo pida.
                        .then(
                            if (index == lastFocusedIndex.intValue) {
                                Modifier.focusRequester(focusRequester)
                            } else {
                                Modifier
                            }
                        )
                        // Usamos onFocusChanged para actualizar nuestro estado SOLO si el item gana el foco.
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                lastFocusedIndex.intValue = index
                            }
                        },
                    contentEntityUI = channel,
                    onClick = { onChannelClick(channel) },
                    onFocus = { onChannelFocus(channel) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(1000.dp))
            }
        }
    }
}

