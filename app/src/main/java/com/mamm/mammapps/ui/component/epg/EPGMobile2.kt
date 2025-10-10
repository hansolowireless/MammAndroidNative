package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.mamm.mammapps.ui.theme.Primary
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.rememberSaveableProgramGuideState
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Composable
fun EPGMobile2(
    modifier: Modifier = Modifier,
    content: List<EPGChannelContent>,
    onEventClicked: (EPGEvent) -> Unit
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

    val now = remember { ZonedDateTime.now() }
    val state = rememberSaveableProgramGuideState()

    // Tu corrección, que es la forma idónea de hacerlo.
    LaunchedEffect(Unit) {
        state.snapToCurrentTime()
    }

    ProgramGuide(
        state = state,
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

                val localStartTime = program.startDateTime!!.withZoneSameInstant(localZoneId)
                val localEndTime = program.endDateTime!!.withZoneSameInstant(localZoneId)

                var startHour = localStartTime.hour + localStartTime.minute / 60f
                var endHour = localEndTime.hour + localEndTime.minute / 60f

                // CASO 1: El programa empezó el día anterior.
                // Lo ajustamos para que empiece al inicio del día actual (00:00).
                if (localStartTime.dayOfYear < localEndTime.dayOfYear && localEndTime.dayOfYear == now.dayOfYear) {
                    startHour = 0.0f
                }
                // CASO 2: El programa termina el día siguiente.
                // Lo cortamos al final del día actual (24:00).
                else if (localEndTime.dayOfYear > localStartTime.dayOfYear && localStartTime.dayOfYear == now.dayOfYear) {
                    endHour = 24.0f
                }

                ProgramGuideItem.Program(
                    channelIndex = channelIndex,
                    startHour = startHour,
                    endHour = endHour,
                )
            },
            itemContent = { (program, _) ->
                ProgramCell(
                    program = program,
                    onClick = {
                        onEventClicked(program)
                    }
                )
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

        currentTime(
            layoutInfo = {
                val currentHour = now.hour + now.minute / 60f
                ProgramGuideItem.CurrentTime(hour = currentHour)
            },
            itemContent = {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .background(Primary)
                )
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
private fun ProgramCell(
    program: EPGEvent,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (program.isLive()) Color.Red else Color.Gray)
            .border(1.dp, Color.White)
            .padding(4.dp)
            .clickable { onClick() },
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
