package com.mamm.mammapps.data.model.diagnostic

import com.google.gson.annotations.SerializedName

data class DiagResponseDto(
    @SerializedName("NodeHA") val nodeHA: Node,
    @SerializedName("NodeDir") val nodeDir: Node
)

data class Node(
    @SerializedName("Node01") val node01: String,
    @SerializedName("Node02") val node02: String
)
