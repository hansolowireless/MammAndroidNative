package com.mamm.mammapps.domain.model.loginwithcode

data class LoginData(
    val token: String?,
    val userId: Int?,
    val jsonFile: String?,
    val pinparental: String?,
    val jwtoken: String?,
    val refreshToken: String?,
    val skin: Skin?,
    val channelOrder: String?,
    val loginUser: String? = null,
    val operator: String? = null
)

data class Skin (
    val operator: String?,
    val logos: List<SkinLogo>?
)

data class SkinLogo(
    val type: String?,
    val url: String?
)
