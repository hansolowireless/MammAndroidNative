package com.mamm.mammapps.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mamm.mammapps.util.isAndroidTV

val LocalIsTV = compositionLocalOf { false }

@Composable
fun DeviceProvider(content: @Composable () -> Unit) {
    val context = LocalContext.current

    val isTV = remember {
        isAndroidTV(context)
    }

    CompositionLocalProvider(LocalIsTV provides isTV) {
        content()
    }
}
