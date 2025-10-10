package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Composable
fun EPGMobile2(
    modifier: Modifier = Modifier,
    content: List<EPGChannelContent>
) {
    val programsWithChannelIndex = remember(content) {
        content.flatMapIndexed { channelIndex, channelContent ->
            channelContent.events.mapNotNull { program ->
                if (program.startDateTime != null && program.endDateTime != null) {
                    program to channelIndex
                } else {
                    null
                }
            }
        }
    }

    ProgramGuide(
        modifier = modifier.fillMaxSize()
    ) {
        channels(
            count = content.size,
            layoutInfo = { channelIndex ->
                ProgramGuideItem.Channel(index = channelIndex)
            },
            itemContent = { channelIndex ->
                ChannelCell(row = content[channelIndex])
            }
        )

        programs(
            items = programsWithChannelIndex,
            layoutInfo = { (program, channelIndex) ->
                val localZoneId = ZoneOffset.systemDefault()

                // --- ESTA ES LA CORRECCIÓN ---
                // Tu `program.startDateTime` YA es un ZonedDateTime en UTC.
                // No necesitamos construirlo. Solo lo usamos para convertirlo a la zona local.
                val localStartTime = program.startDateTime!!.withZoneSameInstant(localZoneId)
                val localEndTime = program.endDateTime!!.withZoneSameInstant(localZoneId)
                // --- FIN DE LA CORRECCIÓN ---

                val startHour = localStartTime.hour + localStartTime.minute / 60f
                val endHour = localEndTime.hour + localEndTime.minute / 60f

                ProgramGuideItem.Program(
                    channelIndex = channelIndex,
                    startHour = startHour,
                    endHour = endHour,
                )
            },
            itemContent = { (program, _) ->
                ProgramCell(program = program)
            }
        )

        val timelineHours = 0..23
        timeline(
            count = timelineHours.count(),
            layoutInfo = { index ->
                val hour = timelineHours.toList()[index].toFloat()
                ProgramGuideItem.Timeline(
                    startHour = hour,
                    endHour = hour + 1f
                )
            },
            itemContent = { index ->
                TimelineCell(hour = timelineHours.toList()[index])
            }
        )
    }
}

@Composable
private fun ChannelCell(row: EPGChannelContent, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.DarkGray)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = row.channel.toContentEPGUI().imageUrl,
            contentDescription = row.channel.toContentEPGUI().title,
            contentScale = ContentScale.Fit,
        )
    }
}

@Composable
private fun ProgramCell(program: EPGEvent, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Gray)
            .border(1.dp, Color.White)
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = program.getTitle(),
            color = Color.White,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
    }
}

@Composable
private fun TimelineCell(hour: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.LightGray)
            .border(1.dp, Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$hour:00",
            color = Color.Black
        )
    }
}
