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

/**
 * VideoResizeManager que implementa un enfoque directo para posicionar el video
 * en la parte superior y mantener la relación de aspecto 16:9.
 */
open class VideoResizeManager(
    private val fragment: Fragment,
    private val frameLayoutId: Int
) {

    companion object {
        // Factor de escala para el tamaño reducido (70% de la altura original)
        private const val SMALL_SIZE_SCALE = 0.789f
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

    private val mainHandler = Handler(Looper.getMainLooper())

    // Runnable para redimensionamiento automático
    private val resizeRunnable = object : Runnable {
        override fun run() {
            if (autoResizeEnabled) {
                if (currentSize == VideoSize.FULL_SIZE) {
                    Log.d("VideoResizeManager", "Reduciendo tamaño automáticamente")
                    resizeTo(VideoSize.SMALL_SIZE)

                    mainHandler.postDelayed({
                        if (autoResizeEnabled && currentSize != VideoSize.FULL_SIZE) {
                            Log.d("VideoResizeManager", "Volviendo a tamaño completo automáticamente")
                            resizeTo(VideoSize.FULL_SIZE)
                            mainHandler.postDelayed(this, autoResizeIntervalMs)
                        }
                    }, smallSizeDurationMs)
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

            // Configurar constraints para mantener en la parte superior
            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID

            // Eliminar cualquier enlace a la parte inferior
            params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
            params.bottomToTop = ConstraintLayout.LayoutParams.UNSET

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
     * Activa o desactiva el redimensionamiento automático
     */
    fun setAutoResize(enabled: Boolean, intervalSecs: Long? = 0, smallDurationSecs: Long? = 0) {
        Log.d("VTManager", "Ticker function for this content is enabled: $enabled, intervalo: $intervalSecs, duracion: $smallDurationSecs")
        mainHandler.removeCallbacks(resizeRunnable)

        autoResizeEnabled = enabled
        if (intervalSecs != null && intervalSecs > 0) {
            autoResizeIntervalMs = intervalSecs.times(1000)
        }

        if (smallDurationSecs != null && smallDurationSecs > 0) {
            smallSizeDurationMs = smallDurationSecs.times(1000)
        }

        if (enabled) {
            Log.d("VideoResizeManager", "Iniciando redimensionamiento automático")

            // Si comenzamos reducidos, volver a tamaño completo
            if (currentSize != VideoSize.FULL_SIZE) {
                resizeTo(VideoSize.FULL_SIZE)
            }

            // Iniciar el ciclo
            mainHandler.postDelayed(resizeRunnable, autoResizeIntervalMs)
        } else {
            Log.d("VideoResizeManager", "Redimensionamiento automático desactivado")
            release()
        }
    }

    /**
     * Libera recursos
     */
    open fun release() {
        mainHandler.removeCallbacks(resizeRunnable)
        autoResizeEnabled = false

        // Restaurar tamaño original
        if (currentSize != VideoSize.FULL_SIZE && originalHeight > 0) {
            forceResize(false)
        }
    }

    // Interface para notificar cambios
    interface OnVideoResizeListener {
        fun onVideoResized(newSize: VideoSize)
    }

    open var resizeListener: OnVideoResizeListener? = null
}