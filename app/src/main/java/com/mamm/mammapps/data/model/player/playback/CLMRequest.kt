package com.mamm.mammapps.data.model.player.playback

import com.google.gson.annotations.SerializedName

data class CLMRequest(
    @field:SerializedName("") // Sin nombre de parámetro
    val typeOfContentString: String,

    @field:SerializedName("user")
    val user: String,

    @field:SerializedName("model")
    val model: String,

    @field:SerializedName("type")
    val deviceType: String,

    @field:SerializedName("operator")
    val operator: String,

    @field:SerializedName("jwt")
    val jwt: String
) {
    fun toQueryMap(): Map<String, String> {
        return mapOf(
            "user" to user,
            "model" to model,
            "type" to deviceType,
            "operator" to operator,
            "jwt" to jwt
        )
    }
}
