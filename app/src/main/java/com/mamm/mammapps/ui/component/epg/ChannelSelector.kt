package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed // Importante: usar itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.theme.Dimensions
import kotlinx.coroutines.delay

@Composable
fun ChannelSelector(
    modifier: Modifier = Modifier,
    channels: List<ContentEPGUI>,
    selectedChannelId: Int? = null,
    onChannelSelected: (ContentEPGUI) -> Unit = {}
) {

    ProvideLazyListPivotOffset (parentFraction = 0.0f) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            // 1. Usamos 'itemsIndexed' para obtener el índice de cada canal
            itemsIndexed(channels, key = { _, channel -> channel.id }) { index, channel ->
                val isSelected = (selectedChannelId != null && selectedChannelId == channel.id) || index == 0
                var isFocused by remember { mutableStateOf(false) }
                // 2. Cada elemento necesita un FocusRequester para ser enfocado individualmente
                val focusRequester = remember { FocusRequester() }

                // 3. Este efecto se lanza para cada item que entra en la composición.
                //    Al comprobar 'index == 0', nos aseguramos de que solo el primer
                //    elemento de la lista solicite el foco, que coincidirá con el
                //    primer elemento visible cuando el Composable se muestre por primera vez.
                LaunchedEffect(Unit) {
                    if (isSelected) {
                        delay(100)
                        focusRequester.requestFocus()
                    }
                }

                val containerColor = when {
                    isFocused -> MaterialTheme.colorScheme.secondaryContainer
                    isSelected -> Color.Transparent
                    else -> Color.Transparent
                }

                ListItem(
                    modifier = Modifier
                        // 4. Asignamos el focusRequester a cada item
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (focusState.isFocused) {
                                onChannelSelected(channel)
                            }
                        }
                        .clip(RoundedCornerShape(12.dp))
                        .focusable(),
                    headlineContent = {
                        Text(
                            text = channel.title,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    leadingContent = {
                        AsyncImage(
                            model = channel.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Fit
                        )
                    },
                    trailingContent = if (isSelected) {
                        {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else null,
                    colors = ListItemDefaults.colors(
                        containerColor = containerColor
                    )
                )
            }
        }
    }
}
