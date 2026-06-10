package com.mamm.mammapps.data.mapper

import com.mamm.mammapps.data.model.epg.EPGChannelContentDto
import com.mamm.mammapps.data.model.epg.MultiDayEPGDto
import com.mamm.mammapps.data.model.section.EPGEventDto
import com.mamm.mammapps.domain.model.epg.EPGChannelContent
import com.mamm.mammapps.domain.model.epg.MultiDayEPG
import com.mamm.mammapps.domain.model.LogoTransition
import com.mamm.mammapps.domain.model.entity.Event

fun EPGEventDto.toDomain(): Event {
    return Event(
        id = this.idEvent?.toIntOrNull() ?: this.idEvent?.hashCode(),
        channelById = this.idChannel?.toIntOrNull(),
        logoTransitions = this.tbEventLogoTransitions?.map { LogoTransition(url = it) },
        eventLogoUrl500 = this.eventLogoUrl500,
        posterLogo = this.posterLogo,
        eventLogoUrl = this.eventLogoUrl,
        fcIni = this.fcIni,
        fcEnd = this.fcEnd,
        deliveryURL = this.deliveryUrl,
        subgenreById = this.idSubgenre?.toIntOrNull(),
        parental = this.idParental?.toIntOrNull(),
        
        // Mapped values
        title = this.getTitle(),
        description = this.getDescription(),
        subtitle = this.getSubtitle(),
        metadata = this.getMetadata().toDomain()
    )
}

fun EPGChannelContentDto.toDomain(): EPGChannelContent {
    return EPGChannelContent(
        channel = this.channel.toDomain(),
        events = this.events.map { it.toDomain() }
    )
}

fun MultiDayEPGDto.toDomain(): MultiDayEPG {
    return MultiDayEPG(
        multiDayEPG = this.multiDayEPG.mapValues { (_, contentList) ->
            contentList.map { it.toDomain() }
        }
    )
}
