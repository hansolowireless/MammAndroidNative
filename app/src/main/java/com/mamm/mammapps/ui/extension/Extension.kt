package com.mamm.mammapps.ui.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.bumptech.glide.Glide
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.WatermarkInfo
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.floor

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
fun String.landscape() = this.replace(".png", "_viewer.png").replace(".jpg", "_viewer.jpg")
fun String.adult(isAdult: Boolean = false) : String {
    return if (isAdult) this.replace(".png", "_p.png").replace(".jpg", "_p.jpg") else this
}

fun String.buildThumbnailUrl(position: Long?): String {
    requireNotNull(position) { "buildThumbnailUrl position cannot be null" }
    val urlPattern = when {
        contains("smil:") -> "smil:"
        contains("nopack03-") -> "nopack03-"
        else -> "nopack-"
    }

    val contentID = substringAfter(urlPattern).substringBefore("_", substringBefore("/"))
    val thumbnailNumber = (floor(position / 500000.0) + 1).toInt()
    val thumbnailNumberString = thumbnailNumber.toString().padStart(3, '0')

    return "${substringBefore("/$urlPattern")}-img/${contentID}_mf$thumbnailNumberString.jpg"
}

fun ImageView.loadWatermarkOrHide(watermarkInfo: WatermarkInfo) {
    if (watermarkInfo.url.isNullOrEmpty()) {
        this.visibility = View.GONE
        return
    }

    this.visibility = if (watermarkInfo.has) View.VISIBLE else View.GONE
    Glide.with(this.context)
        .load(watermarkInfo.url)
        .placeholder(null)
        .error(R.drawable.mosca_laliga)
        .into(this)
}

fun ZonedDateTime.toDate(): Date {
    return Date(this.toInstant().toEpochMilli())
}

fun TextView.setHourText(date: ZonedDateTime?) {
    this.text = date
        ?.withZoneSameInstant(ZoneId.systemDefault()) // Convierte a la zona horaria del dispositivo
        ?.format(DateTimeFormatter.ofPattern("HH:mm"))
        ?: ""
}

fun Int?.toBookmarkStartTimeMs() : Long {
    return this?.times(1000)?.toLong() ?: 0
}

// Función auxiliar para obtener la Activity
fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No se encontró Activity")
}

