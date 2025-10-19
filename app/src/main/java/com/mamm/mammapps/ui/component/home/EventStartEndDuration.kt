package com.mamm.mammapps.ui.component.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mamm.mammapps.ui.extension.toHHmmString
import com.mamm.mammapps.ui.model.player.LiveEventInfoUI
import com.mamm.mammapps.ui.theme.HomeGridTopColor

@Composable
fun EventStartEndDuration(
    modifier: Modifier = Modifier,
    liveEventInfo: LiveEventInfoUI?,
    duration: String?
) {
    Text(
        text = "${liveEventInfo?.eventStart?.toHHmmString()}-${liveEventInfo?.eventEnd?.toHHmmString()}, $duration min.",
        color = HomeGridTopColor.description,
        style = MaterialTheme.typography.bodyLarge,
        modifier = modifier
    )
}