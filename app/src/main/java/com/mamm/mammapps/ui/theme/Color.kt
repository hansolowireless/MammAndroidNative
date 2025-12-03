package com.mamm.mammapps.ui.theme

import androidx.compose.ui.graphics.Color

val Primary = Color(0xFFF0ED00)
val Secondary = Color.White
val Outlined = Color(0xFF625b71)
val TextPrimary = Color.White
val Background = Color.Black

object ButtonColor {
    val focusedContent = Color.White
    val unfocusedContent = Color.White
    val background = Color.DarkGray
}

object ContentEntityListItemColor {
    val focusedContent = Color.White.copy(alpha = 0.2f)
    val unfocusedContent = Color.Transparent
}

object ContentEntityColor {
    val text = Color.White
    val glow = Color.White
}

object HomeGridTopColor {
    val eventitle = Primary
    val description = Secondary
    val metadata = Primary
}

object HomeGridBottomColor {
    val rowTitle = Color.White
}

object SeasonSelectorTabs {
    val tabTitle = Color.White
}

object SectionTitleColor {
    val title = Primary
}

object DetailColor {
    val title = Primary
    val description = Color.White
    val metadata = Outlined
    val serieChapterBackground = Color.Black.copy(alpha = 0.7f)
}

object EPGMobileColor {
    val channelCellBackground = NetflixColorScheme.outlineVariant
    val timelineTextColor = NetflixColorScheme.outlineVariant

    val eventCellBorder = NetflixColorScheme.secondaryContainer
    val eventCellBackground = NetflixColorScheme.surfaceVariant
    val eventCellBackgroundLive = NetflixColorScheme.primaryContainer
    val eventCellText = NetflixColorScheme.onSurfaceVariant
    val eventCellTextLive = NetflixColorScheme.onPrimaryContainer

    val timeLine = NetflixColorScheme.onError

}

object PlayerColor {
    val channelZappingText = Color.White
}

object ExpandCategoryColor {
    val title = Color.White
}

object SnackbarColor {
    val containerColor = Color.DarkGray
    val contentColor = Color.White
    val actionColor = Color.Yellow
    val dismissActionContentColor = Color.LightGray
}