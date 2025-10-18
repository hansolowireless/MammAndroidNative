package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.theme.Dimensions

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
        items(channels) { channel ->
            val isSelected = selectedChannelId == channel.id

            // 1. Volvemos a necesitar el estado de foco aquí, pero solo para el color
            var isFocused by remember { mutableStateOf(false) }

            val containerColor = when {
                isFocused -> MaterialTheme.colorScheme.secondaryContainer
                isSelected -> Color.Transparent
                else -> Color.Transparent
            }

            ListItem(
                modifier = Modifier
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
    }}
}