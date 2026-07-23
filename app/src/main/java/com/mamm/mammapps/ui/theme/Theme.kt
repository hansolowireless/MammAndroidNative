package com.mamm.mammapps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme as TvMaterialTheme
import com.mamm.mammapps.R

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MammAppsTheme(
    content: @Composable () -> Unit
) {

    Primary = FlavorColorScheme.primary

    MaterialTheme(
        colorScheme = FlavorColorScheme,
        typography = Typography,
    ) {
        // El tema de TV (androidx.tv.material3) es independiente del de Compose
        // Material3, así que hay que darle también la tipografía de marca para que
        // los componentes de TV no caigan a la fuente por defecto del sistema.
        TvMaterialTheme(
            typography = TvBrandTypography,
        ) {
            // Los Text() de Compose sin `style` no heredan la tipografía del tema;
            // fijamos solo la fuente (sin tocar tamaño/color) para que la usen.
            ProvideTextStyle(TextStyle(fontFamily = AppFontFamily)) {
                content()
            }
        }
    }
}
