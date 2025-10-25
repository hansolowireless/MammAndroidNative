package com.mamm.mammapps

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import com.mamm.mammapps.navigation.AppNavigation
import com.mamm.mammapps.ui.component.DeviceProvider
import com.mamm.mammapps.ui.theme.MammAppsTheme
import com.mamm.mammapps.util.isAndroidTV
import com.mamm.mammapps.util.isTablet
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDeviceTablet = isTablet()

        // Establecer la orientación deseada
        // - Móvil: Solo retrato (portrait).
        // - Tablet: Rotación libre según el sensor.
        requestedOrientation = if (isDeviceTablet || isAndroidTV(this)) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        enableEdgeToEdge()
        setContent {
            MammAppsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DeviceProvider {
                        AppNavigation()
                    }
                }
            }
        }
    }
}

