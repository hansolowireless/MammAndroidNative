package com.mamm.mammapps.ui.component.channels

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.common.ContentEntity
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun ChannelGridMobile(
    modifier: Modifier = Modifier,
    channels: List<ContentEntityUI>,
    onChannelClick: (ContentEntityUI) -> Unit,
    onChannelFocus: (ContentEntityUI) -> Unit = {}
) {
    LazyVerticalGrid(
        columns = GridCells.FixedSize(180.dp),
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(
            horizontal = Dimensions.paddingMedium,
            vertical = Dimensions.paddingSmall
        ),
        horizontalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall,
            Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(Dimensions.paddingXSmall)
    ) {
        items(channels) { channel ->
            ContentEntity(
                contentEntityUI = channel,
                onClick = { onChannelClick(channel) },
                onFocus = { onChannelFocus(channel) }
            )
        }
    }
}
