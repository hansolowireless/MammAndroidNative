package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.component.common.SectionTitle
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import com.mamm.mammapps.ui.model.ContentEPGUI
import com.mamm.mammapps.ui.theme.Dimensions
import java.time.LocalDate

@Composable
fun EPGContent(
    epgContent: List<EPGChannelContent>,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    onEventClicked: (EPGEvent) -> Unit
) {

    var selectedChannel by remember { mutableStateOf<ContentEPGUI?>(epgContent.first().channel.toContentEPGUI()) }

    Column {
        SectionTitle(title = "GUÍA DE PROGRAMACIÓN")
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
                events = epgContent.find { it.channel.id == selectedChannel?.id }?.events
                    ?: emptyList(),
                onEventClicked = onEventClicked
            )
        }
    }
}