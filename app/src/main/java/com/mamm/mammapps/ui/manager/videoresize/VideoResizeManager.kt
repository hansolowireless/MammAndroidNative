package com.mamm.mammapps.ui.manager.videoresize

import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.mamm.mammapps.domain.model.player.TickerInfo

/**
 * VideoResizeManager que implementa un enfoque directo para posicionar el video
 * en la parte superior y mantener la relación de aspecto 16:9.
 */
open class VideoResizeManager(
    fragment: Fragment,
    frameLayoutId: Int
) {

    companion object {
        // Factor de escala para el tamaño reducido (70% de la altura original)
        private const val SMALL_SIZE_SCALE = 0.75f
    }

    enum class VideoSize {
        FULL_SIZE,
        SMALL_SIZE
    }

    private val frameLayout: FrameLayout = fragment.view?.findViewById(frameLayoutId)
        ?: throw IllegalArgumentException("No se encontró el FrameLayout del reproductor")

    private val parentLayout: ConstraintLayout = frameLayout.parent as? ConstraintLayout
        ?: throw IllegalArgumentException("El padre del FrameLayout debe ser un ConstraintLayout")

    private var originalHeight = 0
    private var originalWidth = 0

    var currentSize = VideoSize.FULL_SIZE
        private set

    private var autoResizeEnabled = false
    private var autoResizeIntervalMs = 30000L
    private var smallSizeDurationMs = 10000L
    private var cycleEnabled = false

    private val mainHandler = Handler(Looper.getMainLooper())

    // Runnables explícitos para poder cancelar ambos tiempos y evitar solapamientos
    private val resizeToSmallRunnable: Runnable = object : Runnable {
        override fun run() {
            if (autoResizeEnabled && currentSize == VideoSize.FULL_SIZE) {
                Log.d("VideoResizeManager", "Reduciendo tamaño automáticamente")
                resizeTo(VideoSize.SMALL_SIZE)
                mainHandler.postDelayed(resizeToFullRunnable, smallSizeDurationMs)
            }
        }
    }

    private val resizeToFullRunnable: Runnable = object : Runnable {
        override fun run() {
            if (autoResizeEnabled && currentSize != VideoSize.FULL_SIZE) {
                Log.d("VideoResizeManager", "Volviendo a tamaño completo automáticamente")
                resizeTo(VideoSize.FULL_SIZE)
                if (cycleEnabled) {
                    mainHandler.postDelayed(resizeToSmallRunnable, autoResizeIntervalMs)
                }
            }
        }
    }

    init {
        // Capturar dimensiones originales
        frameLayout.post {
            try {
                // Guardar dimensiones originales
                originalHeight = frameLayout.height
                originalWidth = frameLayout.width

                Log.d("VideoResizeManager", "Dimensiones originales medidas: $originalWidth x $originalHeight")

                // Verificar relación de aspecto
                val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
                Log.d("VideoResizeManager", "Relación de aspecto original: $aspectRatio")

                // Si la relación no es 16:9, ajustar el ancho
                if (Math.abs(aspectRatio - (16f/9f)) > 0.1f) {
                    originalWidth = (originalHeight * 16) / 9
                    Log.d("VideoResizeManager", "Ajustando ancho para mantener 16:9: $originalWidth")
                }

                // Configurar layout inicial explícitamente
                configureLayoutParams(frameLayout, originalWidth, originalHeight, true)
            } catch (e: Exception) {
                Log.e("VideoResizeManager", "Error inicializando dimensiones", e)
            }
        }
    }

    /**
     * Configura los parámetros de layout de manera consistente
     */
    private fun configureLayoutParams(view: View, width: Int, height: Int, isFullSize: Boolean) {
        try {
            // Crear nuevos parámetros de layout para evitar cualquier configuración problemática
            val params = ConstraintLayout.LayoutParams(width, height)

            if (isFullSize) {
                // Comportamiento de tamaño completo: anclado arriba y centrado horizontalmente
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
            } else {
                // Comportamiento de tamaño reducido (Ticker): pegado en la esquina superior izquierda
                params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                params.rightToRight = ConstraintLayout.LayoutParams.UNSET
                params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
                params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
            }

            // Configurar márgenes
            params.topMargin = 0
            params.leftMargin = 0
            params.rightMargin = 0

            // Aplicar los nuevos parámetros
            view.layoutParams = params

            // Forzar una actualización inmediata del layout
            parentLayout.requestLayout()

            Log.d("VideoResizeManager", "Parámetros configurados: $width x $height")
        } catch (e: Exception) {
            Log.e("VideoResizeManager", "Error configurando layout params", e)
        }
    }

    /**
     * Redimensiona a un tamaño específico con animación
     */
   open fun resizeTo(targetSize: VideoSize) {
        if (targetSize == currentSize || originalHeight <= 0) {
            return
        }

        val currentHeight = frameLayout.height
        val currentWidth = frameLayout.width

        val targetHeight = if (targetSize == VideoSize.FULL_SIZE) {
            originalHeight
        } else {
            (originalHeight * SMALL_SIZE_SCALE).toInt()
        }

        // Calcular el ancho para mantener relación 16:9
        val targetWidth = (targetHeight * 16) / 9

        Log.d("VideoResizeManager", "Animando de ${currentWidth}x${currentHeight} a ${targetWidth}x${targetHeight}")

        // Configurar parámetros de layout antes de la animación
        configureLayoutParams(frameLayout, currentWidth, currentHeight, targetSize == VideoSize.FULL_SIZE)

        // Usar un animador de valores para la transición
        val animator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600  // Más lento para suavidad
            interpolator = PathInterpolator(0.1f, 0.0f, 0.1f, 1.0f)  // Muy suave

            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                val newHeight = currentHeight + ((targetHeight - currentHeight) * fraction)
                val newWidth = currentWidth + ((targetWidth - currentWidth) * fraction)

                try {
                    // Solo modificar tamaño, no posición
                    val params = frameLayout.layoutParams
                    params.width = newWidth.toInt()
                    params.height = newHeight.toInt()
                    frameLayout.layoutParams = params

                    // Forzar layout
                    parentLayout.requestLayout()
                } catch (e: Exception) {
                    Log.e("VideoResizeManager", "Error en animación", e)
                }
            }
        }

        animator.start()
        currentSize = targetSize

        // Notificar al listener
        resizeListener?.onVideoResized(currentSize)
    }

    /**
     * Procesa eventos de teclas
     */
    fun handleKeyEvent(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                if (currentSize == VideoSize.SMALL_SIZE) {
                    resizeTo(VideoSize.FULL_SIZE)
                    true
                } else false
            }

            KeyEvent.KEYCODE_DPAD_DOWN -> {
                if (currentSize == VideoSize.FULL_SIZE) {
                    resizeTo(VideoSize.SMALL_SIZE)
                    true
                } else false
            }

            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                toggleSize()
                true
            }

            else -> false
        }
    }

    /**
     * Alterna entre tamaño completo y reducido
     */
    private fun toggleSize() {
        if (currentSize == VideoSize.FULL_SIZE) {
            resizeTo(VideoSize.SMALL_SIZE)
        } else {
            resizeTo(VideoSize.FULL_SIZE)
        }
    }

    /**
     * Forzar redimensionamiento inmediato sin animación
     */
    private fun forceResize(small: Boolean) {
        val targetHeight = if (small) {
            (originalHeight * SMALL_SIZE_SCALE).toInt()
        } else {
            originalHeight
        }

        // Calcular el ancho para mantener relación 16:9
        val targetWidth = (targetHeight * 16) / 9

        // Configurar directamente con los nuevos parámetros
        configureLayoutParams(frameLayout, targetWidth, targetHeight, !small)

        currentSize = if (small) VideoSize.SMALL_SIZE else VideoSize.FULL_SIZE
        Log.d("VideoResizeManager", "Forzando redimensionamiento a: ${targetWidth}x${targetHeight}")

        // Notificar al listener
        resizeListener?.onVideoResized(currentSize)
    }

    /**
     * Activa o desactiva el redimensionamiento automático basándose en si hay tickers disponibles.
     * El filtrado de tickers por canal, estado live y canales deshabilitados ya se realiza
     * en el ViewModel antes de llamar a este método.
     *
     * @param tickerInfo Objeto que contiene la lista de tickers ya filtrados
     */
    // Campaña del ticker actualmente mostrado en SMALL_SIZE, para no reprogramar el timer
    // de vuelta a FULL_SIZE si sigue siendo el mismo ticker (p.ej. reemisiones del polling).
    private var activeCampaignId: String? = null

    fun setAutoResize(tickerInfo: TickerInfo?) {

        if (tickerInfo == null) {
            activeCampaignId = null
            Log.d("VideoResizeManager", "TickerInfo nulo, desactivando AutoResize")
            stopAutoResize()
            return
        }

        val hasTickers = tickerInfo.tickers.isNotEmpty()
        val firstTicker = tickerInfo.tickers.firstOrNull()

        if (hasTickers && currentSize == VideoSize.SMALL_SIZE && firstTicker?.campaignId == activeCampaignId) {
            Log.d("VideoResizeManager", "Mismo ticker ya en curso, no se reprograma el timer")
            return
        }

        Log.d("VideoResizeManager", "Configurando AutoResize: hasTickers=$hasTickers")

        // Detener cualquier ejecución previa
        mainHandler.removeCallbacks(resizeToSmallRunnable)
        mainHandler.removeCallbacks(resizeToFullRunnable)

        autoResizeEnabled = hasTickers
        cycleEnabled = false
        activeCampaignId = firstTicker?.campaignId

        if (hasTickers && firstTicker != null) {
            if (firstTicker.tiempoDuracion > 0) {
                smallSizeDurationMs = firstTicker.tiempoDuracion.toLong() * 1000
            }

            Log.d("VideoResizeManager", "AutoResize activado: Duración ${smallSizeDurationMs}ms")

            resizeTo(VideoSize.SMALL_SIZE)
            mainHandler.postDelayed(resizeToFullRunnable, smallSizeDurationMs)
        } else {
            Log.d("VideoResizeManager", "AutoResize desactivado")
            stopAutoResize()
        }
    }

    /**
     * Detiene el ciclo de redimensionamiento automático y restaura el tamaño original
     */
    open fun stopAutoResize() {
        mainHandler.removeCallbacks(resizeToSmallRunnable)
        mainHandler.removeCallbacks(resizeToFullRunnable)
        autoResizeEnabled = false
        activeCampaignId = null

        // Restaurar tamaño original
        if (currentSize != VideoSize.FULL_SIZE && originalHeight > 0) {
            forceResize(false)
        }
    }

    /**
     * Libera recursos
     */
    open fun release() {
        stopAutoResize()
    }

    // Interface para notificar cambios
    interface OnVideoResizeListener {
        fun onVideoResized(newSize: VideoSize)
    }

    open var resizeListener: OnVideoResizeListener? = null
}