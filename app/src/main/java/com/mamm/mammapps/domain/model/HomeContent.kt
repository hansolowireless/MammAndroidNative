package com.mamm.mammapps.domain.model

import com.mamm.mammapps.domain.model.entity.Channel
import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.model.entity.Featured
import com.mamm.mammapps.domain.model.entity.Serie
import com.mamm.mammapps.domain.model.entity.VoD

data class HomeContent(
    val timeGenerated: String? = null,
    val featured: List<Featured>? = null,
    val channels: List<Channel>? = null,
    val contents: List<VoD>? = null,
    val genres: List<Genre>? = null,
    val categories: List<Category>? = null,
    val events: List<Event>? = null,
    val series: List<Serie>? = null
)
