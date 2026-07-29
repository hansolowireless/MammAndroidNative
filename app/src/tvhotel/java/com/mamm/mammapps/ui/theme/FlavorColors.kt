package com.mamm.mammapps.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val FlavorColorScheme = darkColorScheme(
    primary = Color(0xFFFFFFFF),
    onPrimary = Color(0xFF141414),
    primaryContainer = Color(0xFF3A3A3A),
    onPrimaryContainer = Color(0xFFF2F2F2),

    secondary = Color(0xFFE0E0E0),
    onSecondary = Color(0xFF141414),
    secondaryContainer = Color(0xFF4A4A4A),
    onSecondaryContainer = Color(0xFFEDEDED),

    tertiary = Color(0xFFBDBDBD),
    onTertiary = Color(0xFF141414),

    background = Color(0xFF000000),        // Negro Netflix
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
