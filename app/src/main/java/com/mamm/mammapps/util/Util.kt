package com.mamm.mammapps.util

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration


fun isAndroidTV(context: Context): Boolean {
    return try {
        val uiModeManager = context.getSystemService(UiModeManager::class.java)
        uiModeManager?.currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    } catch (e: Exception) {
        false
    }
}