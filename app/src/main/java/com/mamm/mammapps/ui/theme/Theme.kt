package com.mamm.mammapps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.mamm.mammapps.R

@Composable
fun MammAppsTheme(
    content: @Composable () -> Unit
) {

    Primary = FlavorColorScheme.primary

    MaterialTheme(
        colorScheme = FlavorColorScheme,
        typography = Typography,
        content = content
    )
}
