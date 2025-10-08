package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.Channel
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.component.home.SectionTitle
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import com.mamm.mammapps.ui.theme.Dimensions
import java.time.LocalDate

@Composable
fun EPGContent(
    epgContent: List<EPGChannelContent>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (EPGEvent) -> Unit
) {

    var selectedChannel by remember { mutableStateOf<Channel>(epgContent.first().channel) }

    Column {
        SectionTitle(title = stringResource(R.string.nav_epg))
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimensions.paddingMedium)
        ) {
            ChannelsColumn(
                channels = epgContent.map { it.channel.toContentEPGUI() },
                modifier = Modifier.weight(1f),
                selectedChannelId = selectedChannel.id,
                onChannelSelected = { channel ->
                    epgContent.find { it.channel.id == channel.id }?.channel?.let {
                        selectedChannel = it
                    }
                }
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = Dimensions.paddingXSmall),
                thickness = 2.dp
            )

            DatesColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.paddingSmall),
                selectedDate = selectedDate,
                onDateSelected = { date ->
                    onDateSelected(date)
                }
            )

            VerticalDivider(
                modifier = Modifier.padding(horizontal = Dimensions.paddingXSmall),
                thickness = 2.dp
            )

            EventsColumn(
                modifier = Modifier
                    .weight(2f)
                    .padding(horizontal = Dimensions.paddingSmall),
                events = epgContent.find { it.channel.id == selectedChannel.id }?.events
                    ?: emptyList(),
                channel = selectedChannel,
                onEventClicked = onEventClicked
            )
        }
    }
}