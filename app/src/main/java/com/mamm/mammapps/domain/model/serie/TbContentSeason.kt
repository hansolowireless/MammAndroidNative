package com.mamm.mammapps.domain.model.serie

import android.os.Parcelable
import com.mamm.mammapps.domain.model.entity.VoD
import kotlinx.parcelize.Parcelize

@Parcelize
data class TbContentSeason(
    val chapter: String? = null,
    val idContent: String? = null,
    val idSeason: String? = null,
    val contentDetails: VoD? = null,
    val idContentSeason: String? = null
) : Parcelable
