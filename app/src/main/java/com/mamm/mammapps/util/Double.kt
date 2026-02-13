package com.mamm.mammapps.util

fun calculateMbps(bytes: Long, durationMs: Long): Double {
    if (durationMs <= 0) return 0.0
    val bits = bytes * 8.0
    val seconds = durationMs / 1000.0
    return (bits / seconds) / 1_000_000.0
}