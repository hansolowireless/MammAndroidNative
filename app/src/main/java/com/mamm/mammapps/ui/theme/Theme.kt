package com.mamm.mammapps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val NetflixColorScheme = darkColorScheme(
    primary = Color(0xFFF0ED00),           // Amarillo brillante
    onPrimary = Color(0xFF141414),
    primaryContainer = Color(0xFFC4C100),  // Amarillo más oscuro
    onPrimaryContainer = Color(0xFF141414),

    secondary = Color(0xFFFFFF33),         // Amarillo claro
    onSecondary = Color(0xFF141414),
    secondaryContainer = Color(0xFF999100),
    onSecondaryContainer = Color(0xFFFFFFC7),

    tertiary = Color(0xFFD4D100),          // Amarillo dorado
    onTertiary = Color(0xFF141414),

    background = Color(0xFF141414),        // Negro Netflix
    onBackground = Color(0xFFE5E5E5),      // Texto gris claro

    surface = Color(0xFF1F1F1F),           // Gris muy oscuro para cards
    onSurface = Color(0xFFE5E5E5),
    surfaceVariant = Color(0xFF2F2F2F),    // Variante un poco más clara
    onSurfaceVariant = Color(0xFFB3B3B3),

    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),

    outline = Color(0xFF3A3A3A),           // Bordes sutiles
    outlineVariant = Color(0xFF2A2A2A),
)

@Composable
fun MammAppsTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = NetflixColorScheme,
        typography = Typography,
        content = content
    )
}
