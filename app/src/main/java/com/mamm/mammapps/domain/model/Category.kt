package com.mamm.mammapps.domain.model

data class Category(
    val catchupRow: Boolean? = null,
    val pos: Int? = null,
    val name: String? = null,
    val id: Int? = null,
    val loadMore: Boolean = false,
    val order: List<OrderItem>? = null
)

data class OrderItem(
    val pos: Int? = null,
    val id: Int? = null,
    val type: String? = null
)
