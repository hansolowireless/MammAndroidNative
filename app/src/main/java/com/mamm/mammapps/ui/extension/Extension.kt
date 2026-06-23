package com.mamm.mammapps.ui.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
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
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.Player
import com.google.android.gms.cast.MediaMetadata
import com.google.android.gms.common.images.WebImage
import com.mamm.mammapps.R
import com.mamm.mammapps.util.getCurrentDate
import com.mamm.mammapps.data.model.player.GlideThumbnailTransformation
import com.mamm.mammapps.domain.model.player.WatermarkInfo
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import com.mamm.mammapps.ui.model.player.ContentToPlayUI
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import kotlin.math.pow

fun Modifier.glow(
    enabled: Boolean = true,
    color: Color = Color.White,
    alpha: Float = 0.8f,
    cornerRadius: Dp = 10.dp,
    glowRadius: Dp = 24.dp,
    layers: Int = 25 // más capas = transición más suave
) = this.drawBehind {
    if (enabled && alpha > 0f) {
        val cornerRadiusPx = cornerRadius.toPx()
        val glowColor = color
        val glowRadiusPx = glowRadius.toPx()

        for (i in 1..layers) {
            // radio con un incremento progresivo no lineal (curva suave)
            val t = i / layers.toFloat()
            val radius = t * glowRadiusPx
            val decay = (1f - t).pow(2.8f) // curva de caída más suave
            val layerAlpha = alpha * decay * 0.8f // atenúa el centro un poco

            drawRoundRect(
                color = glowColor.copy(alpha = layerAlpha),
                topLeft = Offset(-radius, -radius),
                size = Size(size.width + radius * 2, size.height + radius * 2),
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

fun Int?.secondsToMs(): Long {
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

fun Player.jump10sForward() {
    this.seekTo(this.currentPosition + 10_000L)
}

fun Player?.jump10sBack() {
    this?.seekTo(this.currentPosition - 10_000L)
}

fun ZonedDateTime.toHHmmString(): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return this.format(formatter)
}

fun ImageView.insertThumbnail(url: String?, position: Long, onError: (() -> Unit)? = null) {
    Glide.with(this)
        .load(
            url.toString()
                .buildThumbnailUrl(position)
        )
        .override(
            Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL
        )
        .transform(
            GlideThumbnailTransformation(
                position.mod(PlayerConstant.THUMBNAIL_UPDATE_INTERVAL)
            )
        )
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                onError?.invoke()
                return false // Permite que Glide maneje el placeholder/error drawable
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }
        })
        .into(this)
}

/**
 * Convierte una tecla numérica a su representación como String.
 */
fun Key.toDigitString(): String? {
    return when (this) {
        Key.Zero, Key.NumPad0 -> "0"
        Key.One, Key.NumPad1 -> "1"
        Key.Two, Key.NumPad2 -> "2"
        Key.Three, Key.NumPad3 -> "3"
        Key.Four, Key.NumPad4 -> "4"
        Key.Five, Key.NumPad5 -> "5"
        Key.Six, Key.NumPad6 -> "6"
        Key.Seven, Key.NumPad7 -> "7"
        Key.Eight, Key.NumPad8 -> "8"
        Key.Nine, Key.NumPad9 -> "9"
        else -> null
    }
}

fun ContentEntityUI.catchupIsAvailable(availableCatchupHours: Int): Boolean {
    val startInstant = liveEventInfo?.eventStart?.toInstant() ?: return false
    val nowInstant = getCurrentDate().toInstant()
    val differenceInMinutes = ChronoUnit.MINUTES.between(startInstant, nowInstant)
    val differenceInHours = differenceInMinutes / 60.0

    return availableCatchupHours > 0 &&
            differenceInHours > 0 &&
            differenceInHours < availableCatchupHours
}


/**
 * Convierte un objeto ContentToPlayUI en un MediaMetadata para el SDK de Cast.
 */
fun ContentToPlayUI.toMediaMetadata(): MediaMetadata {
    return MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE)
        .apply {
            putString(MediaMetadata.KEY_TITLE, title)
            putString(MediaMetadata.KEY_SUBTITLE, subtitle.orEmpty())
            runCatching {
                images.add(WebImage(imageUrl.toUri()))
            }
        }
}

fun Int?.bitsToMegabits(): Double {
    return (this?.toDouble() ?: 0.0) / 1_000_000.0
}

fun Double.formatMbps(): String {
    return String.format(java.util.Locale.US, "%.2f", this)
}