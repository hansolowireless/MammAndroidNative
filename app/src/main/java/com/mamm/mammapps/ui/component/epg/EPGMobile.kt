package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.data.model.epg.EPGChannelContent
import dev.sajidali.jctvguide.ChannelCell
import dev.sajidali.jctvguide.ChannelRow
import dev.sajidali.jctvguide.Channels
import dev.sajidali.jctvguide.CurrentDay
import dev.sajidali.jctvguide.EventCell
import dev.sajidali.jctvguide.Events
import dev.sajidali.jctvguide.Header
import dev.sajidali.jctvguide.Selection
import dev.sajidali.jctvguide.TimeCell
import dev.sajidali.jctvguide.Timebar
import dev.sajidali.jctvguide.TvGuide
import dev.sajidali.jctvguide.data.Event
import dev.sajidali.jctvguide.data.JctvChannel
import dev.sajidali.jctvguide.utils.formatToPattern
import dev.sajidali.jctvguide.utils.now
import dev.sajidali.jctvguide.utils.rememberGuideState
import java.util.Calendar
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@Composable
fun EPGMobile(
    modifier: Modifier = Modifier,
    content : List<EPGChannelContent>
) {

    val startTime by remember {
        derivedStateOf {
            Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
            }.timeInMillis
        }
    }

    val stopTime by remember {
        derivedStateOf {
            Calendar.getInstance().apply {
                timeInMillis = now
                set(Calendar.HOUR_OF_DAY, 23)
                set(Calendar.MINUTE, 59)
            }.timeInMillis
        }
    }

    val guideState = rememberGuideState(
        startTime = startTime,
        endTime = stopTime,
        hoursInViewport = 2.hours,
        timeSpacing = 30.minutes,
        initialOffset = now
    )

    val channels = remember(content) {
        content.map { epgRow ->
            JctvChannel(
                id = epgRow.channel.id ?: 0,
                title = epgRow.channel.name.orEmpty(),
                icon = ""
            ).also { jctvChannel ->
                jctvChannel.events = epgRow.events.mapNotNull { event ->
                    if (event.startDateTime != null && event.endDateTime != null) {
                        Event(
                            id = event.getId(),
                            title = event.getTitle(),
                            description = event.getDescription(),
                            start = event.startDateTime!!.toInstant().toEpochMilli(),
                            end = event.endDateTime!!.toInstant().toEpochMilli()
                        )
                    } else {
                        null
                    }
                }

            }
        }.toMutableStateList()
    }

    var selected by remember { mutableStateOf(Selection(0,0)) }

    TvGuide(
        state = guideState,
        onStartReached = {  },
        onEndReached = { },
        nowIndicator = {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    strokeWidth = 3f
                )
            }
        }
    ) {
        Header(height = 40.dp) {

            CurrentDay(
                width = 150.dp,
                modifier = Modifier
                    .padding(vertical = 2.dp, horizontal = 4.dp)
                    .background(color = Color.LightGray)
            ) { time: Long ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Text(
                        text = time.formatToPattern("dd-MM"),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            Timebar(
                modifier = Modifier
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                    .background(color = Color.LightGray)
            ) {
                TimeCell(
                    modifier = Modifier.fillMaxHeight()
                ) { time ->
                    Text(
                        text = time.formatToPattern("HH:mm"),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }

        Channels(
            width = 150.dp,
            channels = channels,
            key = { it?.id ?: 0 },
            modifier = Modifier
        ) { channelIndex: Int, channel: JctvChannel?, isSelected ->

            val channelEvents = remember(channelIndex) {
                channels[channelIndex].events
            }

            ChannelRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isSelected) 70.dp else 62.dp)
            ) { _ ->
                ChannelCell(
                    modifier = Modifier
                        .padding(
                            horizontal = 4.dp,
                            vertical = 1.dp
                        )
                        .background(
                            color = if (isSelected) Color.Red else Color.Gray
                        ),
                    onClick = {}
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Text(
                            text = channel?.title ?: "",
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                    }
                }

                Events(
                    modifier = Modifier,
                    events = channelEvents
                ) { event: Event, isEventSelected ->
                    EventCell(
                        modifier = Modifier
                            .padding(1.dp)
                            .background(
                                if (isEventSelected) Color.Red else Color.Gray
                            )
                            .padding(start = 8.dp),
                        onSelected = {
                            selected = Selection(
                                channelIndex,
                                channels[channelIndex].events.indexOf(event)
                            )
                        },
                        onClick = {

                        }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = event.title,
                                color = Color.White
                            )
                        }
                    }

                }
            }
        }
    }

}