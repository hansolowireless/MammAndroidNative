package com.mamm.mammapps.ui.manager.videoresize

import android.animation.ValueAnimator
import android.graphics.Paint
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.view.animation.PathInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.Ticker
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Versión final con arquitectura corregida. Usa un bucle de control único.
 */
class VideoResizeManagerWithTickerCompose(
    private var tickerList: List<Ticker>
) : DefaultLifecycleObserver {

    // --- Propiedades ---
    private var rootViewRef: WeakReference<View>? = null
    private var playerViewRef: WeakReference<StyledPlayerView>? = null
    private var lifecycleOwnerRef: WeakReference<LifecycleOwner>? = null
    private var tickerContainer: FrameLayout? = null
    private var tickerTextView: TextView? = null
    private var tickerBackground: ImageView? = null

    private var currentSize = VideoSize.FULL_SIZE
    private var originalHeight = 0
    private var originalWidth = 0
    private val scope = MainScope()
    private var cycleJob: Job? = null
    private var tickerLoopJob: Job? = null // El nuevo bucle de control
    private var sizeAnimator: ValueAnimator? = null
    private var tickerAnimator: ValueAnimator? = null

    private var currentTickerIndex = -1
    private var currentTextIndex = -1
    private var _ticker: Ticker? = null
    private val paint = Paint()
    private val tickerHeightDp = 90f

    companion object {
        private const val TAG = "VTManagerCompose"
        private const val SMALL_SIZE_SCALE = 0.789f
        private const val ANIMATION_SPEED_FACTOR = 15f
        private const val PAUSE_BETWEEN_TEXTS = 500L
        private const val PAUSE_FOR_STATIC_TEXT = 5000L

        // *** CONTROL TOTAL SOBRE LOS BORDES ***
        private const val ANIMATION_HORIZONTAL_MARGIN_DP = 120f
    }

    // --- Métodos de inicialización y ciclo principal (sin cambios) ---
    fun initialize(rootView: View, lifecycleOwner: LifecycleOwner) {
        this.rootViewRef = WeakReference(rootView)
        this.lifecycleOwnerRef = WeakReference(lifecycleOwner)
        lifecycleOwner.lifecycle.addObserver(this)

        this.playerViewRef = WeakReference(rootView.findViewById(R.id.player_view))
        this.tickerContainer = WeakReference<FrameLayout>(rootView.findViewById(R.id.ticker_container)).get()
        this.tickerTextView = WeakReference<TextView>(rootView.findViewById(R.id.ticker_text)).get()
        this.tickerBackground = WeakReference<ImageView>(rootView.findViewById(R.id.ticker_background_image)).get()

        val playerView = playerViewRef?.get()
        if (playerView == null || tickerContainer == null || tickerTextView == null || tickerBackground == null) {
            Log.e(TAG, "Una o más vistas clave no se encontraron.")
            return
        }

        playerView.post {
            originalHeight = playerView.height
            originalWidth = playerView.width
            val firstValidTicker = tickerList.firstOrNull { it.isValid() }
            if (firstValidTicker != null) {
                startCycle(
                    intervalMs = firstValidTicker.tiempoEntreApariciones.toLong() * 1000,
                    smallDurationMs = firstValidTicker.tiempoDuracion.toLong() * 1000
                )
            }
        }
    }

    private fun startCycle(intervalMs: Long, smallDurationMs: Long) {
        cycleJob?.cancel()
        cycleJob = scope.launch {
            if (currentSize != VideoSize.FULL_SIZE) resizeTo(VideoSize.FULL_SIZE)
            while (true) {
                delay(intervalMs)
                if (tickerList.any { it.isValid() }) {
                    resizeTo(VideoSize.SMALL_SIZE)
                    delay(smallDurationMs)
                    if (currentSize != VideoSize.FULL_SIZE) resizeTo(VideoSize.FULL_SIZE)
                }
            }
        }
    }

    private fun resizeTo(targetSize: VideoSize) {
        if (targetSize == currentSize && targetSize == VideoSize.SMALL_SIZE) return
        if (originalHeight <= 0) return

        animateSize(targetSize)
        currentSize = targetSize

        if (targetSize == VideoSize.SMALL_SIZE) {
            // Cuando se reduce, SE INICIA EL BUCLE DE CONTROL
            showTickerAndStartLoop()
        } else {
            // Cuando se expande, SE DETIENE EL BUCLE DE CONTROL
            hideTickerAndStopLoop()
        }
    }

    // --- NUEVA ARQUITECTURA ---

    private fun showTickerAndStartLoop() {
        // 1. Prepara el siguiente Ticker (no el texto, el Ticker completo)
        advanceToNextValidTicker()
        tickerBackground?.visibility = View.VISIBLE
        tickerContainer?.visibility = View.VISIBLE

        // 2. Cancela cualquier bucle anterior y lanza el nuevo
        tickerLoopJob?.cancel()
        tickerLoopJob = scope.launch {
            // Este bucle se ejecuta mientras el ticker sea visible
            while (isTickerVisible()) {
                // 3. Avanza al siguiente texto DENTRO del bucle
                advanceToNextText()

                // 4. Espera a que la animación de este texto termine (o la pausa si es estático)
                val success = animateCurrentText()
                if (!success) {
                    // Si algo falla (ej: texto vacío), espera y continúa el bucle
                    delay(PAUSE_BETWEEN_TEXTS)
                }

                // 5. Pequeña pausa antes de mostrar el siguiente texto
                delay(PAUSE_BETWEEN_TEXTS)
            }
        }
    }

    private suspend fun animateCurrentText(): Boolean {
        val tickerText = tickerTextView ?: return false
        val container = tickerContainer ?: return false

        // Usamos una suspendCoroutine para "esperar" a que el post termine.
        val isReady = suspendCoroutine { continuation ->
            tickerText.post {
                val ready = container.width > 0 && isTickerVisible()
                continuation.resume(ready)
            }
        }
        if (!isReady || tickerText.text.isNullOrEmpty()) return false

        // --- CÁLCULOS (AHORA SÍ, LOS CORRECTOS) ---
        val textWidth = calculateTextWidth(tickerText)
        val containerWidth = container.width.toFloat()
        val marginPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, ANIMATION_HORIZONTAL_MARGIN_DP, container.resources.displayMetrics
        )

        val safeAreaWidth = containerWidth - (2 * marginPx)

        // DECISIÓN: Animar o centrar
        return if (textWidth <= safeAreaWidth) {
            // Texto estático: centrar y esperar
            tickerText.translationX = marginPx + (safeAreaWidth - textWidth) / 2
            delay(PAUSE_FOR_STATIC_TEXT)
            true
        } else {
            // Texto largo: animar y esperar a que termine
            // La animación SIEMPRE empieza fuera de la pantalla.
            val startPosition = containerWidth
            // La animación termina cuando el final del texto llega al margen izquierdo.
            val endPosition = marginPx - textWidth

            val duration = ((startPosition - endPosition) * ANIMATION_SPEED_FACTOR).toLong().coerceIn(3000L, 30000L)

            // Usamos una suspendCoroutine para esperar el final de la animación
            suspendCoroutine { continuation ->
                // **¡CLAVE!** Reseteamos la posición inicial explícitamente
                tickerText.translationX = startPosition

                tickerAnimator = ValueAnimator.ofFloat(startPosition, endPosition).apply {
                    this.duration = duration
                    this.interpolator = LinearInterpolator()
                    addUpdateListener {
                        if (tickerText.parent != null) {
                            tickerText.translationX = it.animatedValue as Float
                        }
                    }
                    doOnEnd {
                        // Cuando la animación termina, reanuda la corrutina del bucle principal
                        continuation.resume(Unit)
                    }
                }
                tickerAnimator?.start()
            }
            true
        }
    }

    private fun hideTickerAndStopLoop() {
        // Detiene el bucle de control y cualquier animación en curso
        tickerLoopJob?.cancel()
        tickerLoopJob = null
        stopAnimation() // Un método de limpieza más simple

        tickerContainer?.visibility = View.GONE
        tickerBackground?.visibility = View.GONE
    }

    // Método de limpieza simplificado
    private fun stopAnimation() {
        tickerAnimator?.removeAllListeners()
        tickerAnimator?.cancel()
        tickerAnimator = null
    }

    // --- Métodos de Ayuda (ligeramente modificados) ---

    private fun advanceToNextValidTicker() {
        if (tickerList.none { it.isValid() }) return
        var attempts = 0
        do {
            currentTickerIndex = (currentTickerIndex + 1) % tickerList.size
            if (attempts++ > tickerList.size) return
        } while (!tickerList[currentTickerIndex].isValid())
        setCurrentTicker()
    }

    private fun setCurrentTicker() {
        if (tickerList.isEmpty() || currentTickerIndex !in tickerList.indices) return
        _ticker = tickerList[currentTickerIndex]
        currentTextIndex = -1 // Resetea el índice de texto para cada nuevo Ticker
        setTickerImageRemote()
    }

    private fun advanceToNextText() {
        val textos = _ticker?.textos.orEmpty()
        if (textos.isEmpty()) {
            setTickerText("")
            return
        }
        currentTextIndex = (currentTextIndex + 1) % textos.size
        setTickerText(textos[currentTextIndex])
    }

    // --- Métodos de bajo nivel (sin cambios) ---

    private fun setTickerText(text: String?) {
        tickerTextView?.text = text ?: ""
        tickerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f)
    }

    private fun calculateTextWidth(textView: TextView): Float {
        paint.textSize = textView.textSize
        paint.typeface = textView.typeface
        return paint.measureText(textView.text.toString())
    }

    private fun setTickerImageRemote() {
        val context = rootViewRef?.get()?.context ?: return
        val imageUrl = _ticker?.fondo ?: return
        tickerBackground?.let {
            Glide.with(context).load(imageUrl).apply(RequestOptions().centerCrop()).into(it)
        }
    }

    fun isTickerVisible(): Boolean = tickerContainer?.visibility == View.VISIBLE

    // --- Limpieza del ciclo de vida ---
    override fun onDestroy(owner: LifecycleOwner) = release()

    private fun release() {
        scope.cancel() // Cancela todos los jobs (cycleJob, tickerLoopJob)
        sizeAnimator?.cancel()
        stopAnimation()
        lifecycleOwnerRef?.get()?.lifecycle?.removeObserver(this)
        rootViewRef?.clear()
        playerViewRef?.clear()
        lifecycleOwnerRef?.clear()
    }

    // --- Métodos restantes sin cambios ---
    private fun animateSize(targetSize: VideoSize) {
        val playerView = playerViewRef?.get() ?: return
        sizeAnimator?.cancel()
        val params = playerView.layoutParams as FrameLayout.LayoutParams
        val tickerHeightPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tickerHeightDp, playerView.resources.displayMetrics).toInt()
        val startHeight = playerView.height.toFloat()
        val startWidth = playerView.width.toFloat()
        val startMargin = params.bottomMargin
        val targetHeight: Float; val targetWidth: Float; val targetMargin: Int; val targetGravity: Int
        if (targetSize == VideoSize.FULL_SIZE) {
            targetHeight = originalHeight.toFloat(); targetWidth = originalWidth.toFloat(); targetMargin = 0; targetGravity = Gravity.CENTER
        } else {
            targetHeight = originalHeight * SMALL_SIZE_SCALE; targetWidth = originalWidth * SMALL_SIZE_SCALE; targetMargin = tickerHeightPx; targetGravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }
        sizeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600; interpolator = PathInterpolator(0.1f, 0.0f, 0.1f, 1.0f)
            addUpdateListener {
                val fraction = it.animatedValue as Float
                params.height = (startHeight + (targetHeight - startHeight) * fraction).toInt()
                params.width = (startWidth + (targetWidth - startWidth) * fraction).toInt()
                params.bottomMargin = (startMargin + (targetMargin - startMargin) * fraction).toInt()
                playerView.layoutParams = params
            }
            doOnEnd {
                params.gravity = targetGravity
                if (targetSize == VideoSize.FULL_SIZE) {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT; params.height = ViewGroup.LayoutParams.MATCH_PARENT; params.bottomMargin = 0
                } else {
                    params.height = targetHeight.toInt(); params.width = targetWidth.toInt(); params.bottomMargin = targetMargin
                }
                playerView.layoutParams = params
            }
        }
        sizeAnimator?.start()
    }

    fun replaceTickers(newTickerList: List<Ticker>) {
        this.tickerList = newTickerList
        if (cycleJob == null || cycleJob?.isActive == false) {
            val firstValidTicker = tickerList.firstOrNull { it.isValid() }
            if (firstValidTicker != null) {
                startCycle(
                    intervalMs = firstValidTicker.tiempoEntreApariciones.toLong() * 1000,
                    smallDurationMs = firstValidTicker.tiempoDuracion.toLong() * 1000
                )
            }
        }
    }

    enum class VideoSize { FULL_SIZE, SMALL_SIZE }
}
