package com.mamm.mammapps.domain.model.entity

enum class FeaturedFormat {
    LIVE, VOD, CUTV, STILL, BANNER, UNKNOWN;

    companion object {
        fun from(value: String?): FeaturedFormat = when (value?.lowercase()) {
            "live" -> LIVE
            "vod" -> VOD
            "cutv" -> CUTV
            "still" -> STILL
            "banner" -> BANNER
            else -> UNKNOWN
        }
    }
}
