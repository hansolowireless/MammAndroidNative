package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import coil.compose.AsyncImage
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.EPGEvent
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.model.ContentDetailUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun EventsColumn(
    events: List<EPGEvent>,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(events) {
        val liveEventIndex = events.indexOfFirst { it.isLive() }
        if (liveEventIndex != -1) {
            listState.scrollToItem(liveEventIndex)
        }
    }

    ProvideLazyListPivotOffset(parentFraction = 0.03f) {
        LazyColumn(
            state = listState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            items(events) { event ->
                var isFocused by remember { mutableStateOf(false) }

                Box {
                    ListItem(
                        selected = false,
                        onClick = {
                            //TODO
                        },
                        modifier = Modifier.onFocusChanged { isFocused = it.isFocused },
                        headlineContent = {
                            Text(
                                text = event.getTitle(),
                                style = MaterialTheme.typography.titleSmall,
                                maxLines = if (isFocused) 2 else 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        supportingContent = if (event.isLive() || isFocused) {
                            event.getDescription().takeIf { it.isNotBlank() }?.let { description ->
                                {
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        } else null,
                        trailingContent = event.eventLogoUrl500?.let { imageUrl ->
                            {
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = event.tbEventLanguages?.firstOrNull()?.title,
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(1000.dp))
            }
        }
    }
}

