package com.mamm.mammapps.domain.model.entity

import android.os.Parcelable
import com.mamm.mammapps.domain.model.metadata.Metadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class VoD(
    val id: Int? = null,
    val title: String = "",
    val shortDesc: String = "",
    val longDesc: String = "",
    val duration: Int? = null,
    val deliveryURL: String? = null,
    val logoURL: String? = null,
    val posterURL: String? = null,
    val subgenreById: Int? = null,
    val metadata: Metadata? = null,

    // Fields from BrandedVod / Episode / SectionVod
    val contentLogo: String? = null,
    
    // Explicit mapped descriptions from DTOs
    val rawDescription: String = "",
    val shortDescription: String = ""
) : Parcelable {

    fun getId(): Int {
        return id ?: 0
    }

    fun getDescription(): String {
        return rawDescription.takeIf { it.isNotBlank() }
            ?: longDesc.takeIf { it.isNotBlank() }
            ?: shortDesc.takeIf { it.isNotBlank() }
            ?: shortDescription
    }
}
