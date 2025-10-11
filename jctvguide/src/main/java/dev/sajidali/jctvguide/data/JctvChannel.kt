package dev.sajidali.jctvguide.data

data class JctvChannel(val id: Int, val title: String, val icon: String, var events: List<Event> = emptyList())