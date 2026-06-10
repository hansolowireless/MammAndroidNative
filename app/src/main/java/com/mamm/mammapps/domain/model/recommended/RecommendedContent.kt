package com.mamm.mammapps.domain.model.recommended

import com.mamm.mammapps.domain.model.entity.VoD
import com.mamm.mammapps.domain.model.entity.Event

data class RecommendedContent(
    val vods: List<VoD>? = null,
    val cutvs: List<Event>? = null
)
