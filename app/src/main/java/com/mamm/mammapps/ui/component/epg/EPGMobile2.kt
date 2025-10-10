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
import java.time.format.DateTimeFormatter

@Composable
fun EPGMobile2(
    modifier: Modifier = Modifier,
    content: List<EPGChannelContent>
) {
    // 1. Pre-procesamos la lista para tener los programas y el índice de su canal.
    // Filtramos los programas con fechas nulas para evitar errores.
    val programsWithChannelIndex = remember(content) {
        content.flatMapIndexed { channelIndex, channelContent ->
            channelContent.events.mapNotNull { program ->
                // Solo incluimos programas con fechas válidas
                if (program.startDateTime != null && program.endDateTime != null) {
                    // Creamos un par: el programa y el índice de su canal
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
        // SLOT 1: Definir los canales
        channels(
            count = content.size,
            layoutInfo = { channelIndex ->
                ProgramGuideItem.Channel(index = channelIndex)
            },
            itemContent = { channelIndex ->
                ChannelCell(row = content[channelIndex])
            }
        )

        // SLOT 2: Definir los programas (eventos)
        programs(
            items = programsWithChannelIndex, // Usamos la lista pre-procesada
            layoutInfo = { (program, channelIndex) -> // Desestructuramos el Par
                // Ahora estamos seguros de que las fechas no son nulas
                val startHour = program.startDateTime!!.hour + program.startDateTime!!.minute / 60f
                val endHour = program.endDateTime!!.hour + program.endDateTime!!.minute / 60f

                ProgramGuideItem.Program(
                    channelIndex = channelIndex, // Usamos el índice que ya calculamos
                    startHour = startHour,
                    endHour = endHour,
                )
            },
            itemContent = { (program, _) -> // Ignoramos el índice aquí
                ProgramCell(program = program)
            }
        )

        // SLOT 3: Definir la línea de tiempo
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
            model = row.channel.toContentEPGUI().imageUrl, // Esto ya estaba correcto
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
