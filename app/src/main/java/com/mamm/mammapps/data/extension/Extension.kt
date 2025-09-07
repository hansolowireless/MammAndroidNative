package com.mamm.mammapps.data.extension

import com.mamm.mammapps.data.model.GetHomeContentResponse
import com.mamm.mammapps.data.model.Metadata
import java.time.LocalDate
import java.time.format.DateTimeFormatter


fun GetHomeContentResponse.transformData() = apply {
    contents?.forEach { content ->
        content.metadata = Metadata.fromTbContentItems(content.tbContentItems ?: emptyList())
    }

    channels?.forEach { channel ->
        channel.deliveryURL =
            channel.deliveryURL?.replace("\${id_channel}", channel.id.toString())
                ?.replace("\${quality}", "HD")
    }
}

fun LocalDate.toEPGRequestDate () : String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}