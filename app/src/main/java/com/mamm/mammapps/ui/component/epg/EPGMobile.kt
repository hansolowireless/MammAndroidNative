package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
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
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.ui.mapper.toContentEPGUI
import com.mamm.mammapps.ui.theme.EPGMobileColor
import eu.wewox.programguide.ProgramGuide
import eu.wewox.programguide.ProgramGuideDefaults
import eu.wewox.programguide.ProgramGuideItem
import eu.wewox.programguide.rememberSaveableProgramGuideState
import java.time.Duration
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.ZonedDateTime
import kotlin.math.roundToInt

@Composable
fun EPGMobile(
    modifier: Modifier = Modifier,
    content: List<EPGChannelContent>,
    onEventClicked: (Event) -> Unit,
    selectedDate: LocalDate,
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
    // La hora actual es solo la posición inicial: la librería la usa cuando no hay una
    // guardada. Al volver del player o del detalle se restaura donde estaba el scroll.
    val state = rememberSaveableProgramGuideState(
        initialOffset = { Offset(getCurrentTimePosition(), 0f) }
    )

    // El selector de fechas lo pinta EPGScreen, por encima de este componente: así se
    // mantiene en pantalla mientras se carga el día nuevo en vez de irse con el spinner.
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

                    // La rejilla va de las 00:00 a las 24:00 del día seleccionado. Cada programa
                    // se mide desde ese arranque y se recorta si se sale por algún extremo: el que
                    // viene de anoche se pega a las 00:00 y el que sigue de madrugada, a las 24:00.
                    //
                    // Se mide en horas transcurridas y no comparando fechas porque un programa que
                    // acaba a las 00:00 del día siguiente tiene que valer 24, no 0. Además así la
                    // noche del cambio de año y la del cambio de hora salen bien.
                    val dayStart = selectedDate.atStartOfDay(localZoneId)

                    ProgramGuideItem.Program(
                        channelIndex = channelIndex,
                        startHour = hoursFrom(dayStart, localStartTime),
                        endHour = hoursFrom(dayStart, localEndTime),
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
    program: Event,
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
                text = program.title,
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

/**
 * Posición de [moment] en la rejilla, en horas transcurridas desde [dayStart],
 * recortada a los bordes del día. Un instante anterior al día vale 0 y uno posterior, 24.
 */
private fun hoursFrom(dayStart: ZonedDateTime, moment: ZonedDateTime): Float =
    (Duration.between(dayStart, moment).toMinutes() / 60f).coerceIn(0f, 24f)
