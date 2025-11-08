package com.mamm.mammapps.ui.component.channels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
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

    // Este LaunchedEffect SÓLO se ejecuta cuando la lista de canales cambia.
    // No se ejecutará durante el scroll.
    LaunchedEffect(channels) {
        delay(100)
        focusRequester.requestFocus()
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
            itemsIndexed(channels, key = { _, channel -> channel.identifier.id }) { index, channel ->
                val channel = channels[index]
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
                    // El onFocus original se puede mantener si se necesita para otra lógica.
                    onFocus = { onChannelFocus(channel) }
                )
            }
        }
    }
}

