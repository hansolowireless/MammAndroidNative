package com.example.openstream_flutter_rw.ui.manager.watermark

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mamm.mammapps.data.model.player.WatermarkInfo
import com.mamm.mammapps.ui.extension.loadWatermarkOrHide
import com.mamm.mammapps.ui.model.player.FingerPrintInfoUI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random


class FingerprintController
{
    private val handler = Handler(Looper.getMainLooper())
    private val watermarkRunnable = WatermarkRunnable()
    private val hideRunnable = HideRunnable()

    private var isRunning = false
    private var fingerprintView: TextView? = null
    private var position: String = "random"
    private var playerContainer: ViewGroup? = null
    private var playerFrameLayout: ViewGroup? = null
    private var defaultText: String? = null

    private var watermarkLogoImageView: ImageView? = null

    /**
     * Set the container and default text (only if not already set)
     */
    fun setup(playerContainer: ViewGroup?, playerFrameLayout: ViewGroup?, defaultText: String? = null) {
        if (this.playerContainer == null) {
            this.playerContainer = playerContainer
        }
        if (this.playerFrameLayout == null) {
            this.playerFrameLayout = playerFrameLayout
        }
        if (this.defaultText == null) {
            this.defaultText = defaultText
        }
    }

    /**
     * Start displaying the watermark.
     */
    fun start(
//        enabled: Boolean = false,
//        interval: Int?,
//        duration: Int?,
//        position: String?,
//        text: String? = null,
        fingerPrintInfo: FingerPrintInfoUI?,
        watermarkInfo: WatermarkInfo? = null
    ) {

        Log.d("WATERMARK", "Watermark enabled " +
                "${fingerPrintInfo?.enabled}, " +
                "interval ${fingerPrintInfo?.interval}, " +
                "duration ${fingerPrintInfo?.duration}, " +
                "position ${fingerPrintInfo?.position}")

        val container = playerContainer ?: return
        val playerFrameLayout = playerFrameLayout ?: return

        //Watermark de la liga
        createAndShowWatermarkLogoImageView(playerFrameLayout, watermarkInfo = watermarkInfo ?: WatermarkInfo(hasInt =  0, url = null))

        if (fingerPrintInfo == null) {
            Log.d("WATERMARK", "Fingeprint info is null, fingeprint stopped and disabled")
            stop()
            return
        }

        if (!fingerPrintInfo.enabled || fingerPrintInfo.interval == null || fingerPrintInfo.duration == null) {
            Log.d("WATERMARK", "Fingerprint info missing, stopping and disabling")
            stop()
            return
        }

        // Parar solo si está corriendo para evitar cancelar inmediatamente
        if (isRunning) {
            stop()
        }

        // Crear TextView solo si no existe
        if (fingerprintView == null) {
            createWatermarkView(container)
        }

        // Inicializar el nuevo watermark
        isRunning = true
        this.position = position ?: "random"

        val totalCycleMs = (fingerPrintInfo.interval + fingerPrintInfo.duration) * 1000
        val durationMs = fingerPrintInfo.duration * 1000

        watermarkRunnable.setup(totalCycleMs.toLong(), durationMs.toLong(), fingerPrintInfo.text ?: defaultText ?: "")

        // Dar un pequeño delay para evitar condiciones de carrera
        handler.postDelayed(watermarkRunnable, 1000)
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * (playerContainer?.context?.resources?.displayMetrics?.density ?: 0.0f)).toInt()
    }

    private fun createWatermarkView(container: ViewGroup) {
        fingerprintView = TextView(playerContainer?.context).apply {
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            visibility = View.GONE
            setBackgroundColor(0x8000FF00.toInt()) // Verde semitransparente
            setTextColor(0xFFFFFFFF.toInt()) // Blanco
            textSize = 16f
            setPadding(
                dpToPx(8), dpToPx(8),
                dpToPx(8), dpToPx(8)
            )
        }
        container.addView(fingerprintView)
    }

    private fun createAndShowWatermarkLogoImageView(container: ViewGroup, watermarkInfo: WatermarkInfo) {
        if (watermarkLogoImageView == null) {
            watermarkLogoImageView = ImageView(playerContainer?.context).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                scaleType = ImageView.ScaleType.CENTER_CROP
                alpha = 1f
            }
            container.addView(watermarkLogoImageView)
        }

        Log.d("WATERMARK", "createAndShowWatermarkLogoImageView has: ${watermarkInfo.has}, url: ${watermarkInfo.url}")
        watermarkLogoImageView?.loadWatermarkOrHide(watermarkInfo)
    }

    /**
     * Stop displaying the watermark.
     */
    fun stop() {
        isRunning = false
        handler.removeCallbacks(watermarkRunnable)
        handler.removeCallbacks(hideRunnable)
        fingerprintView?.visibility = View.GONE
    }

    /**
     * Inner class for watermark scheduling - prevents memory leaks
     */
    private inner class WatermarkRunnable : Runnable {
        var totalInterval: Long = 0
        var duration: Long = 0
        var text: String = ""

        fun setup(totalIntervalMs: Long, durationMs: Long, displayText: String) {
            this.totalInterval = totalIntervalMs
            this.duration = durationMs
            this.text = displayText
        }

        override fun run() {
            if (!isRunning) {
                Log.d("WATERMARK", "run Watermark is not running")
                return
            }

            showWatermark(duration, text)
            handler.postDelayed(this, totalInterval)
        }
    }

    /**
     * Inner class for hiding watermark - reusable runnable
     */
    private inner class HideRunnable : Runnable {
        override fun run() {
            fingerprintView?.let {
                if (it.visibility == View.VISIBLE) {
                    it.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Displays the watermark at the specified position for the given duration.
     */
    private fun showWatermark(duration: Long, text: String) {
        val textView = fingerprintView ?: return
        val container = playerContainer ?: return
        if (!isRunning) return

        Log.d("WATERMARK", SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault()).format(Date()))

        container.post {
            val parentWidth = container.width
            val parentHeight = container.height

            if (parentWidth <= 0 || parentHeight <= 0) {
                Log.w("WATERMARK", "Invalid parent dimensions: ${parentWidth}x${parentHeight}")
                return@post
            }

            textView.text = text
            textView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
            val watermarkWidth = textView.measuredWidth
            val watermarkHeight = textView.measuredHeight

            if (watermarkWidth >= parentWidth || watermarkHeight >= parentHeight) {
                Log.w("WATERMARK", "Watermark too large for container")
                return@post
            }

            val (x, y) = calculatePosition(parentWidth, parentHeight, watermarkWidth, watermarkHeight)

            textView.x = x
            textView.y = y
            textView.visibility = View.VISIBLE

            handler.removeCallbacks(hideRunnable)
            handler.postDelayed(hideRunnable, duration)
        }
    }

    private fun calculatePosition(parentWidth: Int, parentHeight: Int, watermarkWidth: Int, watermarkHeight: Int): Pair<Float, Float> {
        val paddingFactor = 0.9
        val paddedParentWidth = (paddingFactor * parentWidth).toInt()
        val paddedParentHeight = (paddingFactor * parentHeight).toInt()

        val halfWidth = paddedParentWidth / 2
        val halfHeight = paddedParentHeight / 2

        return when (position.lowercase()) {
            "top-left" -> Pair(
                Random.nextInt(0, maxOf(1, halfWidth - watermarkWidth)).toFloat(),
                Random.nextInt(0, maxOf(1, halfHeight - watermarkHeight)).toFloat()
            )
            "top-right" -> Pair(
                Random.nextInt(halfWidth, maxOf(halfWidth + 1, paddedParentWidth - watermarkWidth)).toFloat(),
                Random.nextInt(0, maxOf(1, halfHeight - watermarkHeight)).toFloat()
            )
            "bottom-left" -> Pair(
                Random.nextInt(0, maxOf(1, halfWidth - watermarkWidth)).toFloat(),
                Random.nextInt(halfHeight, maxOf(halfHeight + 1, paddedParentHeight - watermarkHeight)).toFloat()
            )
            "bottom-right" -> Pair(
                Random.nextInt(halfWidth, maxOf(halfWidth + 1, paddedParentWidth - watermarkWidth)).toFloat(),
                Random.nextInt(halfHeight, maxOf(halfHeight + 1, paddedParentHeight - watermarkHeight)).toFloat()
            )
            else -> { // "random"
                val maxX = maxOf(0, parentWidth - watermarkWidth)
                val maxY = maxOf(0, parentHeight - watermarkHeight)
                Pair(
                    if (maxX > 0) Random.nextInt(0, maxX).toFloat() else 0f,
                    if (maxY > 0) Random.nextInt(0, maxY).toFloat() else 0f
                )
            }
        }
    }
}

