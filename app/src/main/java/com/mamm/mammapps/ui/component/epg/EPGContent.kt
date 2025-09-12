package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun EPGContent(epgContent: List<EPGChannelContent>) {

    var selectedChannel by remember { mutableStateOf<ContentEPGUI?>(epgContent.first().channel.toContentEPGUI()) }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimensions.paddingMedium)
    ) {
        ChannelsColumn(
            channels = epgContent.map { it.channel.toContentEPGUI() },
            modifier = Modifier.weight(1f),
            selectedChannelId = selectedChannel?.id,
            onChannelSelected = { channel ->
                selectedChannel = channel
            }
        )

        VerticalDivider(modifier = Modifier.padding(horizontal = 8.dp), thickness = 2.dp)

        DatesColumn(
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(Dimensions.paddingSmall))

        EventsColumn(
            events = epgContent.find { it.channel.id.toString() == selectedChannel?.id }?.events ?: emptyList(),
            modifier = Modifier.weight(2f)
        )
    }
}