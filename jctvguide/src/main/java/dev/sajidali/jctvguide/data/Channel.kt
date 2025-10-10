package dev.sajidali.jctvguide.data

import dev.sajidali.jctvguide.data.Event

data class Channel(val id: Int, val title: String, val icon: String, var events: List<Event> = emptyList())