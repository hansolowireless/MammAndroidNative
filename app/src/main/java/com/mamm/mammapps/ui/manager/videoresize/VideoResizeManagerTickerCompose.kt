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

/**
 * Versión final y definitiva.
 * Anima el PlayerView y le aplica un bottomMargin para dejar espacio al ticker,
 * eliminando todos los problemas de temporización.
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
    private var tickerAnimationJob: Job? = null
    private var sizeAnimator: ValueAnimator? = null
    private var tickerAnimator: ValueAnimator? = null

    private var currentTickerIndex = -1
    private var currentTextIndex = -1
    private var _ticker: Ticker? = null
    private val paint = Paint()

    // *** AJUSTE 1: Aumentamos el espacio para que el texto suba más ***
    private val tickerHeightDp = 90f // Lo subimos un poco más para asegurar

    companion object {
        private const val TAG = "VTManagerCompose"
        private const val SMALL_SIZE_SCALE = 0.789f
        private const val ANIMATION_SPEED_FACTOR = 5f
        private const val TEXT_WIDTH_MARGIN = 300
        private const val PAUSE_BETWEEN_CYCLES = 500L
        private const val MIN_ANIMATION_DURATION = 3000L
        private const val MAX_ANIMATION_DURATION = 15000L
    }

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
            Log.e(TAG, "Una o más vistas clave no se encontraron. Gestor desactivado.")
            return
        }

        playerView.post {
            originalHeight = playerView.height
            originalWidth = playerView.width
            Log.d(TAG, "Dimensiones originales del PlayerView: $originalWidth x $originalHeight")

            val firstValidTicker = tickerList.firstOrNull { it.isValid() }
            if (firstValidTicker != null) {
                Log.d(TAG, "Ticker válido encontrado. Iniciando ciclo automático.")
                startCycle(
                    intervalMs = firstValidTicker.tiempoEntreApariciones.toLong() * 1000,
                    smallDurationMs = firstValidTicker.tiempoDuracion.toLong() * 1000
                )
            } else {
                Log.d(TAG, "No se encontraron tickers válidos. El gestor permanecerá inactivo, la tickerlist es $tickerList")
            }
        }
    }

    private fun startCycle(intervalMs: Long, smallDurationMs: Long) {
        cycleJob?.cancel()
        cycleJob = scope.launch {
            if (currentSize != VideoSize.FULL_SIZE) {
                resizeTo(VideoSize.FULL_SIZE)
            }
            while (true) {
                delay(intervalMs)
                if (tickerList.any { it.isValid() }) {
                    Log.d(TAG, "Ciclo: Reduciendo a SMALL_SIZE")
                    resizeTo(VideoSize.SMALL_SIZE)
                    delay(smallDurationMs)
                    if (currentSize != VideoSize.FULL_SIZE) {
                        Log.d(TAG, "Ciclo: Volviendo a FULL_SIZE")
                        resizeTo(VideoSize.FULL_SIZE)
                    }
                } else {
                    Log.w(TAG, "Ciclo: La lista de tickers ahora está vacía o no es válida, se omite esta iteración.")
                }
            }
        }
    }

    private fun resizeTo(targetSize: VideoSize) {
        if (targetSize == currentSize && targetSize == VideoSize.SMALL_SIZE) return // Evitar re-animaciones innecesarias
        if (originalHeight <= 0) return

        animateSize(targetSize)
        currentSize = targetSize

        if (targetSize == VideoSize.SMALL_SIZE) {
            advanceToNextValidTicker()
            tickerBackground?.visibility = View.VISIBLE
            tickerContainer?.visibility = View.VISIBLE
            tickerContainer?.post { startTickerAnimation() }
        } else {
            hideTicker()
        }
    }

    private fun animateSize(targetSize: VideoSize) {
        val playerView = playerViewRef?.get() ?: return
        sizeAnimator?.cancel()

        val currentHeight = playerView.height.toFloat()
        val currentWidth = playerView.width.toFloat()
        val params = playerView.layoutParams as FrameLayout.LayoutParams
        val currentMargin = params.bottomMargin

        val targetHeight: Float
        val targetWidth: Float
        val targetMargin: Int

        // Convertir la altura del ticker de DP a Píxeles
        val tickerHeightPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            tickerHeightDp,
            playerView.resources.displayMetrics
        ).toInt()

        if (targetSize == VideoSize.FULL_SIZE) {
            targetHeight = originalHeight.toFloat()
            targetWidth = originalWidth.toFloat()
            targetMargin = 0 // Sin margen
            params.gravity = Gravity.CENTER // Centrado en pantalla completa
        } else { // SMALL_SIZE
            targetHeight = originalHeight * SMALL_SIZE_SCALE
            targetWidth = originalWidth * SMALL_SIZE_SCALE
            targetMargin = tickerHeightPx // Dejamos espacio para el ticker
            // *** LA CORRECCIÓN DEFINITIVA: Volvemos a anclarlo ARRIBA ***
            params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        }

        sizeAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            duration = 600
            interpolator = PathInterpolator(0.1f, 0.0f, 0.1f, 1.0f)
            addUpdateListener { animation ->
                val fraction = animation.animatedValue as Float
                // Animamos tamaño y margen simultáneamente
                params.height = (currentHeight + (targetHeight - currentHeight) * fraction).toInt()
                params.width = (currentWidth + (targetWidth - currentWidth) * fraction).toInt()
                params.bottomMargin = (currentMargin + (targetMargin - currentMargin) * fraction).toInt()
                playerView.layoutParams = params
            }
            doOnEnd {
                // Al finalizar, aseguramos los valores finales para un estado limpio
                if (targetSize == VideoSize.FULL_SIZE) {
                    params.width = ViewGroup.LayoutParams.MATCH_PARENT
                    params.height = ViewGroup.LayoutParams.MATCH_PARENT
                    params.bottomMargin = 0
                    params.gravity = Gravity.CENTER
                } else {
                    params.height = targetHeight.toInt()
                    params.width = targetWidth.toInt()
                    params.bottomMargin = targetMargin
                    // *** LA CORRECCIÓN DEFINITIVA TAMBIÉN AQUÍ ***
                    params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                }
                playerView.layoutParams = params
            }
        }
        sizeAnimator?.start()
    }

    // --- El resto de la clase permanece igual ---

    private fun hideTicker() {
        stopTickerAnimation()
        tickerContainer?.visibility = View.GONE
        tickerBackground?.visibility = View.GONE
    }

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
        if (tickerList.isEmpty() || currentTickerIndex >= tickerList.size) return
        _ticker = tickerList[currentTickerIndex]
        currentTextIndex = -1
        setTickerImageRemote()
    }

    private fun advanceToNextText() {
        val textos = _ticker?.textos.orEmpty()
        if (textos.isEmpty()) return
        currentTextIndex = (currentTextIndex + 1) % textos.size
        setTickerText(textos[currentTextIndex])
    }

    private fun setTickerText(text: String?) {
        tickerTextView?.text = text ?: ""
        // *** AJUSTE 2: Aumentamos el tamaño de la fuente ***
        tickerTextView?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26f) // Lo subo a 26f, un valor más grande
    }

    private fun setTickerImageRemote() {
        val context = rootViewRef?.get()?.context ?: return
        val imageUrl = _ticker?.fondo ?: return
        tickerBackground?.let {
            Glide.with(context).load(imageUrl).apply(RequestOptions().centerCrop()).into(it)
        }
    }

    private fun startTickerAnimation() {
        stopTickerAnimation()
        val tickerText = tickerTextView ?: return
        val container = tickerContainer ?: return
        advanceToNextText()
        if (container.width <= 0) {
            container.post { startTickerAnimation() }
            return
        }
        val textWidth = calculateTextWidth(tickerText)
        val containerWidth = container.width.toFloat()
        val startPosition = containerWidth
        val endPosition = -textWidth
        val duration = ((startPosition - endPosition) * ANIMATION_SPEED_FACTOR).toLong().coerceIn(MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION)
        createAnimation(tickerText, startPosition, endPosition, duration)
    }

    private fun createAnimation(tickerText: TextView, start: Float, end: Float, duration: Long) {
        tickerAnimator = ValueAnimator.ofFloat(start, end).apply {
            this.duration = duration
            interpolator = LinearInterpolator()
            addUpdateListener {
                if (tickerText.parent != null) {
                    tickerText.translationX = it.animatedValue as Float
                }
            }
            doOnEnd {
                tickerAnimationJob = scope.launch {
                    delay(PAUSE_BETWEEN_CYCLES)
                    if (isTickerVisible()) {
                        startTickerAnimation()
                    }
                }
            }
            start()
        }
    }

    private fun calculateTextWidth(textView: TextView): Float {
        paint.textSize = textView.textSize
        paint.typeface = textView.typeface
        return paint.measureText(textView.text.toString()) + TEXT_WIDTH_MARGIN
    }

    private fun stopTickerAnimation() {
        tickerAnimationJob?.cancel()
        tickerAnimator?.cancel()
    }

    fun replaceTickers(newTickerList: List<Ticker>) {
        this.tickerList = newTickerList
        if (cycleJob == null || cycleJob?.isActive == false) {
            val firstValidTicker = tickerList.firstOrNull { it.isValid() }
            if (firstValidTicker != null) {
                Log.d(TAG, "La lista de tickers se actualizó con elementos válidos. Reiniciando ciclo.")
                startCycle(
                    intervalMs = firstValidTicker.tiempoEntreApariciones.toLong() * 1000,
                    smallDurationMs = firstValidTicker.tiempoDuracion.toLong() * 1000
                )
            }
            else {
                Log.i(TAG, "No hay tickers válidos en la nueva lista. El ciclo no se reiniciará.")
            }
        }
    }

    fun isTickerVisible(): Boolean = tickerContainer?.visibility == View.VISIBLE

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    private fun release() {
        Log.d(TAG, "Liberando todos los recursos.")
        scope.cancel()
        sizeAnimator?.cancel()
        tickerAnimator?.cancel()
        lifecycleOwnerRef?.get()?.lifecycle?.removeObserver(this)
        rootViewRef?.clear()
        playerViewRef?.clear()
        lifecycleOwnerRef?.clear()
    }

    enum class VideoSize { FULL_SIZE, SMALL_SIZE }
}
