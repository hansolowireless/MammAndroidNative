package com.mamm.mammapps.domain.model.loginwithcode

enum class LocatorOrigin {
    SPAIN, COLOMBIA, UNKNOWN;

    companion object {
        fun from(value: String?): LocatorOrigin = when (value) {
            "MSM" -> SPAIN
            "CO" -> COLOMBIA
            else -> UNKNOWN
        }
    }
}
