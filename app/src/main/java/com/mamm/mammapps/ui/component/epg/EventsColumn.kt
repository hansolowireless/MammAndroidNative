package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.data.extension.catchupIsAvailable
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.component.common.ContentEntityListItem
import com.mamm.mammapps.ui.component.common.ProvideLazyListPivotOffset
import com.mamm.mammapps.ui.mapper.toContentListUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun EventsColumn(
    events: List<EPGEvent>,
    catchupHours: Int?,
    modifier: Modifier = Modifier,
    onEventClicked: (EPGEvent) -> Unit = {}
) {
    val listState = rememberLazyListState()

    LaunchedEffect(events) {
        val liveEventIndex = events.indexOfFirst { it.isLive() }
        if (liveEventIndex != -1) {
            listState.scrollToItem(liveEventIndex)
        }
    }

    ProvideLazyListPivotOffset(parentFraction = 0.00f) {
        LazyColumn(
            state = listState,
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(Dimensions.paddingSmall)
        ) {
            items(events) { event ->
                Box {
                    ContentEntityListItem(
                        content = event.toContentListUI(),
                        showLiveIndicator = event.isLive(),
                        showCatchupIndicator = event.catchupIsAvailable(availableCatchupHours = catchupHours ?: 0),
                        onClick = { onEventClicked(event) }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(1000.dp))
            }
        }
    }
}

