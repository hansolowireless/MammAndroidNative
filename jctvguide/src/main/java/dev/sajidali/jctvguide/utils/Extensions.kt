package dev.sajidali.jctvguide.utils

import dev.sajidali.jctvguide.data.Event
import dev.sajidali.jctvguide.data.EventWithIndex
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.byUnicodePattern
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes


fun Long.roundToNearest(timeSpacing: Duration): Long {
    return this - (this % timeSpacing.inWholeMilliseconds)
}

fun Long.roundToNext(timeSpacing: Duration): Long {
    val next = this + timeSpacing.inWholeMilliseconds
    return next - (next % timeSpacing.inWholeMilliseconds)
}

fun List<Event>.load(range: IntRange, duration: Duration, append: Boolean = true) = buildList {
    val firstEvent = this@load[range.first]
    val lastEvent = this@load[range.last]
    val viewportStart = firstEvent.start - if (!append) duration.inWholeMilliseconds else 0
    val viewportEnd = lastEvent.end + if (append) duration.inWholeMilliseconds else 0



    for (i in range.last until size) {
        val event = this@load[i]
        if (event.start > viewportEnd) {
            break
        }
        if (event.end >= viewportStart) {
            add(EventWithIndex(i, event))
        }
    }
}

fun List<Event>.visibleRange(
    viewportStart: Long,
    duration: Duration,
    start: Int = 0,
    end: Int = size
): IntRange {
    val viewportEnd = viewportStart + duration.inWholeMilliseconds
    val firstVisibleIndex = binarySearch(fromIndex = start, toIndex = end) {
        when {
            it.end < viewportStart -> -1
            it.start > viewportEnd || it.start > viewportStart -> 1
            else -> 0
        }
    }
    val firstIndex = if (firstVisibleIndex < 0) 0 else firstVisibleIndex

    var endIndex = size
    for (i in firstIndex until size) {
        val event = get(i)
        if (event.start > viewportEnd) {
            break
        }
        if (event.end >= viewportStart) {
            endIndex = i
        }
    }

    return firstIndex..endIndex

}

val now
    get() = Clock.System.now().toEpochMilliseconds()

fun List<Event>.findVisibleEvents(
    viewportStart: Float,
    viewportEnd: Float,
    startIndex: Int = 0,
    endIndex: Int = size
): List<EventWithIndex> {

    val firstVisibleIndex = binarySearch(fromIndex = startIndex, toIndex = endIndex) {
        when {
            it.end < viewportStart -> -1
            it.start > viewportEnd || it.start > viewportStart -> 1
            else -> 0
        }
    }

    val firstIndex = if (firstVisibleIndex < 0) -firstVisibleIndex - 1 else firstVisibleIndex

    val visibleEvents = mutableListOf<EventWithIndex>()

    for (i in firstIndex until size) {
        val event = get(i)
        if (event.start > viewportEnd) {
            break
        }
        if (event.end >= viewportStart) {
            visibleEvents.add(EventWithIndex(i, event))
        }
    }

    return visibleEvents

}

fun generateEvents(channel: Int, start: Long, stop: Long): List<Event> {
    var startTime = start
    var i = 1
    return buildList {
        while (startTime < stop) {
            val endTime =
                if (startTime.plus(30.minutes.inWholeMilliseconds) >= stop) stop else startTime.plus(
                    Random.nextInt(30, 120).minutes.inWholeMilliseconds
                )
            add(
                Event(
                    i,
                    "Event $i",
                    "Description of event $i",
                    startTime,
                    endTime
                ).also {
                    startTime = endTime
                })
            i++
        }
    }
}


fun Long.formatToPattern(pattern: String): String {
    return Instant.fromEpochMilliseconds(this)
        .format(DateTimeComponents.Format {
            byUnicodePattern(pattern)
        })
}