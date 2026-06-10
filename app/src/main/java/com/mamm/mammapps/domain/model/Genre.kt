package com.mamm.mammapps.domain.model

data class Genre(
    val subgenres: List<Subgenre>? = null,
    val logo: String? = null, // Using String? instead of Any? to be clean, or keeping Any? if it can be non-string
    val id: Int? = null,
    val ds: String? = null
)

data class Subgenre(
    val descripcion: String? = null,
    val logo: String? = null,
    val id: Int? = null,
    val ds: String? = null
)
