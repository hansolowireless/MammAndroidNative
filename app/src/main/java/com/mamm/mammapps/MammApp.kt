package com.mamm.mammapps

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MammApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializaciones generales
    }
}