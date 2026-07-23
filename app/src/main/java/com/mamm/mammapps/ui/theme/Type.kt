package com.mamm.mammapps.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Typography as TvTypography

object FontSizes {
    val xsmall = 8.sp
    val small = 12.sp
    val medium = 16.sp
    val large = 18.sp
    val XL = 24.sp
    val XXL = 36.sp
    val XXXL = 48.sp
    val XXXXL = 60.sp
}

object LineHeights {
    val xsmall = 8.sp
    val small = 12.sp
    val medium = 16.sp
    val large = 20.sp
    val XL = 28.sp
    val XXL = 32.sp
}


// Set of Material typography styles to start with
val Typography = Typography(
    bodySmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSizes.small,
        lineHeight = LineHeights.small,
        letterSpacing = 0.5.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = FontSizes.large,
        lineHeight = LineHeights.large,
        letterSpacing = 0.5.sp
    ),
    titleLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = FontSizes.XL,
        lineHeight = LineHeights.XL,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = FontSizes.medium,
        lineHeight = LineHeights.medium,
        letterSpacing = 0.sp
    ),
    headlineLarge = Typography().headlineLarge.copy(
        fontFamily = AppFontFamily
    ),
    headlineMedium = Typography().headlineMedium.copy(
        fontFamily = AppFontFamily
    ),
    headlineSmall = Typography().headlineSmall.copy(
        fontFamily = AppFontFamily
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

// Tipografía para las superficies de TV (androidx.tv.material3), que es un tema
// independiente del de Compose Material3. Aplica AppFontFamily a todos los estilos
// conservando los tamaños/line-height por defecto de TV.
@OptIn(ExperimentalTvMaterial3Api::class)
val TvBrandTypography: TvTypography = TvTypography().let { base ->
    base.copy(
        displayLarge = base.displayLarge.copy(fontFamily = AppFontFamily),
        displayMedium = base.displayMedium.copy(fontFamily = AppFontFamily),
        displaySmall = base.displaySmall.copy(fontFamily = AppFontFamily),
        headlineLarge = base.headlineLarge.copy(fontFamily = AppFontFamily),
        headlineMedium = base.headlineMedium.copy(fontFamily = AppFontFamily),
        headlineSmall = base.headlineSmall.copy(fontFamily = AppFontFamily),
        titleLarge = base.titleLarge.copy(fontFamily = AppFontFamily),
        titleMedium = base.titleMedium.copy(fontFamily = AppFontFamily),
        titleSmall = base.titleSmall.copy(fontFamily = AppFontFamily),
        bodyLarge = base.bodyLarge.copy(fontFamily = AppFontFamily),
        bodyMedium = base.bodyMedium.copy(fontFamily = AppFontFamily),
        bodySmall = base.bodySmall.copy(fontFamily = AppFontFamily),
        labelLarge = base.labelLarge.copy(fontFamily = AppFontFamily),
        labelMedium = base.labelMedium.copy(fontFamily = AppFontFamily),
        labelSmall = base.labelSmall.copy(fontFamily = AppFontFamily),
    )
}