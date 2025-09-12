package com.mamm.mammapps.ui.extension

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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

fun Modifier.onTap(onTap: () -> Unit): Modifier {
    return this.onKeyEvent { keyEvent ->
        if (keyEvent.key == Key.DirectionCenter && keyEvent.type == KeyEventType.KeyDown) {
            onTap()
            true
        } else {
            false
        }
    }
}

@Composable
fun Modifier.focusableWithColors(
    isSelected: Boolean = false,
    focusedColor: Color = MaterialTheme.colorScheme.primaryContainer,
    selectedColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    defaultColor: Color = MaterialTheme.colorScheme.surface,
    onFocusChanged: (Boolean) -> Unit = {}
): Modifier {
    var isFocused by remember { mutableStateOf(false) }

    return this
        .onFocusChanged { focusState ->
            isFocused = focusState.isFocused
            onFocusChanged(focusState.isFocused)
        }
        .focusable()
        .background(
            color = when {
                isFocused -> focusedColor
                isSelected -> selectedColor
                else -> defaultColor
            },
            shape = RoundedCornerShape(12.dp)
        )
}

fun String.squared() = this.replace(".png", "_x4.png").replace(".jpg", "_x4.jpg")

