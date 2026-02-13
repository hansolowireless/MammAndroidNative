package com.mamm.mammapps.util

import kotlin.math.absoluteValue
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Int?.orRandom() : Int {
    return this ?: Uuid.random().hashCode()
}

@OptIn(ExperimentalUuidApi::class)
fun getRandomHashCode() : Int {
    return Uuid.random().hashCode().absoluteValue
}