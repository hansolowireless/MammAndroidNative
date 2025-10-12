package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import com.mamm.mammapps.ui.theme.EPGMobileColor
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideDefaults
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.rememberSaveableProgramGuideState
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Composable
fun EPGMobile(
    modifier: Modifier = Modifier,
    content: List<EPGChannelContent>,
    onEventClicked: (EPGEvent) -> Unit,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
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

    Column {

        DateSelector(
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )

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

            if (selectedDate == now.toLocalDate())
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
                                .background(EPGMobileColor.timeLine)
                        )
                    }
                )
        }
    }
}

@Composable
private fun ChannelCell(row: EPGChannelContent, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EPGMobileColor.channelCellBackground)
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
    // Estado para almacenar la información de la celda
    var cellWidth by remember { mutableStateOf(0) }
    var cellOffset by remember { mutableStateOf(0f) }

    // Obtenemos el ancho de la columna de canales en píxeles.
    val channelWidthPx = with(LocalDensity.current) {
        ProgramGuideDefaults.dimensions.channelWidth.toPx()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(if (program.isLive()) EPGMobileColor.eventCellBackgroundLive else EPGMobileColor.eventCellBackground)
            .border(1.dp, Color.White)
            .onGloballyPositioned { coordinates ->
                cellWidth = coordinates.size.width
                cellOffset = coordinates.positionInWindow().x
            }
            .clipToBounds()
            .padding(4.dp)
            .clickable { onClick() }
    ) {
        // --- LÓGICA DEL TEXTO PEGADIZO (CORREGIDA) ---
        // 'textOffset' es el desplazamiento que aplicaremos al texto.
        val textOffset = when {
            // 1. Si el borde izquierdo de la celda se ha escondido detrás de la columna de canales,
            // empujamos el texto hacia la derecha para compensar.
            cellOffset < channelWidthPx -> channelWidthPx - cellOffset
            // 2. Si el texto se sale por la derecha (opcional, mejora futura)

            // 3. Si la celda está completamente visible, no aplicamos ningún offset.
            else -> 0f
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset { IntOffset(textOffset.roundToInt(), 0) },
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = program.getTitle(),
                color = if (program.isLive()) EPGMobileColor.eventCellTextLive
                else EPGMobileColor.eventCellText,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
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
