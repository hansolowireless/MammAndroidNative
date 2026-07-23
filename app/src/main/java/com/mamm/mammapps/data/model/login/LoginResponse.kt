package com.mamm.mammapps.data.model.login

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("data")
    val data: LoginDataDto?
)

data class LoginDataDto(
    @SerializedName("token")
    val token: String?,
    @SerializedName("user_id")
    val userId: Int?,
    @SerializedName("json_file")
    val jsonFile: String?,
    @SerializedName("pinparental")
    val pinparental: String?,
    @SerializedName("access_token", alternate = ["jwtoken"])
    val jwtoken: String?,
    @SerializedName("refresh_token")
    val refreshToken: String?,
    @SerializedName("skin")
    val skin: SkinDto?,
    @SerializedName("channel_order")
    val channelOrder: String?,
    @SerializedName("login_user")
    val loginUser: String? = null,
    @SerializedName("operator")
    val operator: String? = null
)

data class SkinDto(
    @SerializedName("operator")
    val operator: String?,
    @SerializedName("logos")
    val logos: List<SkinLogoDto>?
)

data class SkinLogoDto(
    @SerializedName("type")
    val type: String?,
    @SerializedName("url")
    val url: String?
)