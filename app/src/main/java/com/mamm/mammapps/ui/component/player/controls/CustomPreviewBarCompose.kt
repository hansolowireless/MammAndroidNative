package com.mamm.mammapps.ui.component.player.controls

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.github.rubensousa.previewseekbar.PreviewLoader
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.TimeBar
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.GlideThumbnailTransformation
import com.mamm.mammapps.ui.component.player.custompreviewbar.CustomPreviewBar
import com.mamm.mammapps.ui.extension.toDate
import java.time.ZonedDateTime
import kotlin.math.floor

@Composable
fun CustomPreviewBarCompose(
    modifier: Modifier = Modifier,
    progress: Long,
    duration: Long,
    bufferedPosition: Long = 0L,
    player: ExoPlayer? = null,
    videoURL: String,
    onSeekStart: () -> Unit = {},
    onSeekMove: (Long) -> Unit = {},
    onSeekStop: (Long, Boolean) -> Unit = { _, _ -> },
    eventHourBegin: ZonedDateTime? = null,
    eventHourEnd: ZonedDateTime? = null,
    tstvMode: Boolean = false,
    isTimeshift: Boolean = false,
    tstvPoint: ZonedDateTime? = null,
) {
    var previewFrameLayout: FrameLayout? by remember { mutableStateOf(null) }
    var imageView: ImageView? by remember { mutableStateOf(null) }

    val previewLoader = remember(videoURL) {
        PreviewLoader { currentPosition, max ->
            if (player?.isPlaying == true) {
                player.playWhenReady = false
            }

            imageView?.let { iv ->
                Glide.with(iv)
                    .load(buildThumbnailUrl(currentPosition, videoURL).toUri())
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .transform(GlideThumbnailTransformation(currentPosition.mod(500000).toLong()))
                    .into(iv)
            }
        }
    }

    Box(modifier = modifier) {
        // Preview Frame
        AndroidView(
            factory = { context ->
                FrameLayout(context).apply {
                    id = R.id.previewFrameLayout
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    visibility = View.INVISIBLE
                    setBackgroundResource(R.drawable.thumbnails_video_frame)

                    val iv = ImageView(context).apply {
                        id = R.id.imageView
                        layoutParams = FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT,
                            FrameLayout.LayoutParams.MATCH_PARENT
                        ).apply {
                            val marginPx =
                                (2 * context.resources.displayMetrics.density + 0.5f).toInt()
                            setMargins(marginPx, marginPx, marginPx, marginPx)
                        }
                        scaleType = ImageView.ScaleType.FIT_XY
                    }
                    addView(iv)

                    previewFrameLayout = this
                    imageView = iv
                }
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.35f)
                .aspectRatio(16f / 9f)
                .padding(16.dp)
        )

        // Custom Preview Bar
        AndroidView(
            factory = { context ->
                CustomPreviewBar(context, null).apply {
                    eventHourBegin?.let { setEventHourBegin(it.toDate()) }
                    eventHourEnd?.let { setEventHourEnd(it.toDate()) }
                    setTstvMode(tstvMode)
                    setIsTimeshift(isTimeshift)
                    tstvPoint?.let { setTstvPoint(it.toDate()) }

                    isPreviewEnabled = true
                    setPreviewAnimationEnabled(true)
                    setAutoHidePreview(true)
                    setPreviewLoader(previewLoader)

                    addListener(object : TimeBar.OnScrubListener {
                        override fun onScrubStart(timeBar: TimeBar, position: Long) {
                            onSeekStart()
                        }

                        override fun onScrubMove(timeBar: TimeBar, position: Long) {
                            onSeekMove(position)
                        }

                        override fun onScrubStop(
                            timeBar: TimeBar,
                            position: Long,
                            canceled: Boolean
                        ) {
                            onSeekStop(position, canceled)
                        }
                    })
                }
            },
            update = { previewBar ->
                previewBar.setDuration(duration)
                previewBar.setPosition(progress)
                previewBar.setBufferedPosition(bufferedPosition)

                eventHourBegin?.let { previewBar.setEventHourBegin(it.toDate()) }
                eventHourEnd?.let { previewBar.setEventHourEnd(it.toDate()) }
                previewBar.setTstvMode(tstvMode)
                previewBar.setIsTimeshift(isTimeshift)
                tstvPoint?.let { previewBar.setTstvPoint(it.toDate()) }

                previewFrameLayout?.let { frameLayout ->
                    previewBar.attachPreviewView(frameLayout)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(26.dp)
        )
    }
}

private fun buildThumbnailUrl(position: Long, playingURL: String): String {
    val contentID = when {
        playingURL.contains("smil:") -> playingURL.substringAfter("smil:").substringBefore("_")
        playingURL.contains("nopack03-") -> playingURL.substringAfter("nopack03-")
            .substringBefore("/")

        else -> playingURL.substringAfter("nopack-").substringBefore("/")
    }

    val subtractZeros = (floor((position / 500000).toDouble()) + 1).toInt()
    val thumbnailNumberString = subtractZeros.toString().padStart(3, '0')

    return when {
        playingURL.contains("smil:") -> "${playingURL.substringBefore("/smil:")}-img/${contentID}_mf${thumbnailNumberString}.jpg"
        playingURL.contains("nopack03-") -> "${playingURL.substringBefore("/nopack03:")}-img/${contentID}_mf${thumbnailNumberString}.jpg"
        else -> "${playingURL.substringBefore("/nopack-")}-img/${contentID}_mf${thumbnailNumberString}.jpg"
    }
}


