package com.mamm.mammapps.ui.model

import android.os.Parcelable
import com.mamm.mammapps.data.model.metadata.Metadata
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailInfoUI (
    val squareLogo: String? = null,
    val subtitle: String = "",
    val description: String = "",
    val subgenreId: Int? = null,
    val metadata: Metadata? = null
) : Parcelable