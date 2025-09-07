package com.mamm.mammapps.ui.extension

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.theme.Primary


fun Modifier.glow(
    enabled: Boolean = true,
    color: Color = Color.White,
    alpha: Float = 0.8f,
    cornerRadius: Dp = 10.dp,
    glowRadius: Dp = 10.dp,
    layers: Int = 5
) = this.drawBehind {
    if (enabled && alpha > 0f) {
        val cornerRadiusPx = cornerRadius.toPx()
        val glowColor = color.copy(alpha = alpha)

        for (i in 1..layers) {
            val radius = i * (glowRadius.toPx() / layers)
            val layerAlpha = alpha / (i * 1.5f)
            drawRoundRect(
                color = glowColor.copy(alpha = layerAlpha),
                topLeft = Offset(-radius, -radius),
                size = Size(
                    size.width + radius * 2,
                    size.height + radius * 2
                ),
                cornerRadius = CornerRadius(cornerRadiusPx + radius)
            )
        }
    }
}
