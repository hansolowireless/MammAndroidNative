package com.mamm.mammapps.domain.model

import com.mamm.mammapps.domain.model.entity.Event
import com.mamm.mammapps.domain.model.entity.VoD

data class OtherContent(
    val events: List<Event>? = null,
    val vods: List<VoD>? = null
)
