package com.mamm.mammapps.data.logger

interface Logger {
    fun debug(tag: String, message: String)
    fun info(tag: String, message: String)
    fun error(tag: String, message: String)
}