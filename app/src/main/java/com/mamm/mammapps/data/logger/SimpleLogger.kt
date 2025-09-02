package com.mamm.mammapps.data.logger

import android.util.Log

class SimpleLogger : Logger {

    override fun debug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun info(tag: String, message: String) {
        Log.i(tag, message)
    }

    override fun error(tag: String, message: String) {
        Log.e(tag, message)
    }
}