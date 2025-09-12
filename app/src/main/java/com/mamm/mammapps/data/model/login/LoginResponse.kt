package com.mamm.mammapps.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: LoginData?
)

data class LoginData(
    @SerializedName("token")
    val token: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("json_file")
    val jsonFile: String?,
    @SerializedName("pinparental")
    val pinparental: String?,
    @SerializedName("jwtoken")
    val jwtoken: String?,
    @SerializedName("skin")
    val skin: Skin?,
    @SerializedName("channel_order")
    val channelOrder: String?
)

data class Skin(
    @SerializedName("operator")
    val operator: String?,
    @SerializedName("logos")
    val logos: List<SkinLogo>?
)

data class SkinLogo(
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?
)