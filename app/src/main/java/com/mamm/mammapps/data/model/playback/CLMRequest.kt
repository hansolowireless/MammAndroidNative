package com.mamm.mammapps.data.model.playback

import com.google.gson.annotations.SerializedName

data class CLMRequest(
    @field:SerializedName("") // Sin nombre de parámetro
    val typeOfContentString: String,

    @field:SerializedName("user")
    val user: String,

    @field:SerializedName("model")
    val model: String,

    @field:SerializedName("deviceType")
    val deviceType: String,

    @field:SerializedName("operator")
    val operator: String,

    @field:SerializedName("jwt")
    val jwt: String
)
