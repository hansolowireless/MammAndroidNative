package com.mamm.mammapps.ui.extension

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.mamm.mammapps.R
import com.mamm.mammapps.data.extension.getCurrentDate
import com.mamm.mammapps.data.logger.SimpleLogger
import com.mamm.mammapps.data.model.player.GlideThumbnailTransformation
import com.mamm.mammapps.data.model.player.WatermarkInfo
import com.mamm.mammapps.data.model.section.EPGEvent
import com.mamm.mammapps.ui.constant.PlayerConstant
import com.mamm.mammapps.ui.model.ContentEntityUI
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
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

fun String.squared() = this.replace(".png", "_x4.png").replace(".jpg", "_x4.jpg")
fun String.landscape() = this.replace(".png", "_viewer.png").replace(".jpg", "_viewer.jpg")
fun String.adult(isAdult: Boolean = false): String {
    return if (isAdult) this.replace(".png", "_p.png").replace(".jpg", "_p.jpg") else this
}

fun String.buildThumbnailUrl(position: Long?): String {
    requireNotNull(position) { "buildThumbnailUrl position cannot be null" }

    val contentID = when {
        contains("smil:") -> substringAfter("smil:").substringBefore("_")
        contains("nopack03-") -> substringAfter("nopack03-").substringBefore("/")
        else -> substringAfter("nopack-").substringBefore("/")
    }

    val thumbnailNumber =
        (floor(position / PlayerConstant.THUMBNAIL_UPDATE_INTERVAL.toDouble()) + 1).toInt()
    val thumbnailNumberString = thumbnailNumber.toString().padStart(3, '0')

    val baseUrl = when {
        contains("smil:") -> substringBefore("/smil:")
        contains("nopack03-") -> substringBefore("/nopack03-")
        else -> substringBefore("/nopack-")
    }
    val thumbnail = "$baseUrl-img/${contentID}_mf$thumbnailNumberString.jpg"
    SimpleLogger().debug("buildThumbnailUrl", "thumbnail: $thumbnail")
    return "$baseUrl-img/${contentID}_mf$thumbnailNumberString.jpg"
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

fun Int?.toBookmarkStartTimeMs(): Long {
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
 * Comprueba si la tecla corresponde a un número del teclado numérico (Numpad).
 */
fun Key.isNumpadNumber(): Boolean {
    // La forma segura y recomendada es comprobar contra una lista explícita,
    // ya que los valores de 'Key' no son necesariamente secuenciales.
    return this in listOf(
        Key.NumPad0, Key.NumPad1, Key.NumPad2, Key.NumPad3, Key.NumPad4,
        Key.NumPad5, Key.NumPad6, Key.NumPad7, Key.NumPad8, Key.NumPad9
    )
}

/**
 * Convierte una tecla del Numpad a su representación como String.
 * Devuelve el dígito ("0"-"9") si la tecla es del Numpad, o null en caso contrario.
 *
 * Ejemplo: Key.NumPad7.toDigitString() devuelve "7"
 */
fun Key.toDigitString(): String? {
    // Si la tecla no es del Numpad, no hacemos nada.
    if (!this.isNumpadNumber()) return null

    // Usa 'when' para un mapeo seguro y legible.
    return when (this) {
        Key.NumPad0 -> "0"
        Key.NumPad1 -> "1"
        Key.NumPad2 -> "2"
        Key.NumPad3 -> "3"
        Key.NumPad4 -> "4"
        Key.NumPad5 -> "5"
        Key.NumPad6 -> "6"
        Key.NumPad7 -> "7"
        Key.NumPad8 -> "8"
        Key.NumPad9 -> "9"
        else -> null // Seguridad extra, aunque isNumpadNumber() ya protege.
    }
}

fun ContentEntityUI.catchupIsAvailable(availableCatchupHours: Int): Boolean {
    val startInstant = liveEventInfo?.eventStart?.toInstant()
    val nowInstant = getCurrentDate().toInstant()
    val differenceInMinutes = ChronoUnit.MINUTES.between(startInstant, nowInstant)
    val differenceInHours = differenceInMinutes / 60.0

    return availableCatchupHours > 0 &&
            differenceInHours > 0 &&
            differenceInHours < availableCatchupHours
}