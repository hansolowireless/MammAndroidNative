package com.mamm.mammapps.ui.manager.videoresize

import android.animation.ValueAnimator
import android.graphics.Paint
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.animation.doOnEnd
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.mamm.mammapps.R
import com.mamm.mammapps.data.model.player.Ticker
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

//TODO: Quitar LEFT_MARGIN_HIDE_TEXT si el banner cambiase de formato
class VideoResizeManagerWithTicker(
    fragment: Fragment,
    frameLayoutId: Int,
    private var tickerList: List<Ticker>
) : VideoResizeManager(fragment, frameLayoutId), DefaultLifecycleObserver {
    private val fragmentRef = WeakReference(fragment)
    private var currentTickerIndex = 0
    private var currentTextIndex = 0 // Nuevo: índice para los textos del ticker actual

    private var tickerContainer: FrameLayout? = null
    private var tickerTextView: TextView? = null
    private var tickerBackground: ImageView? = null
    private var tickerAnimator: ValueAnimator? = null
    private var _ticker: Ticker? = null

    private var isViewsMeasured = false
    private val paint = Paint()

    private val scope = MainScope()
    private var tickerJob: Job? = null

    // Parámetros configurables
    private var leftAppearPx = 0f
    private var rightAppearPx = 0f
    private var gapBetweenCyclesPx = 200f

    companion object {
        private const val TAG = "VTManager"
        private const val ANIMATION_SPEED_FACTOR = 5f
        private const val TEXT_SIZE_SP = 30f
        private const val TEXT_WIDTH_MARGIN = 300 // Aumentado para textos muy largos
        private const val MIN_ANIMATION_DURATION = 3000L
        private const val MAX_ANIMATION_DURATION = 15000L
        private const val PAUSE_BETWEEN_CYCLES = 500L
        private const val MEASUREMENT_RETRY_DELAY = 100L
        private const val MAX_MEASUREMENT_RETRIES = 5
    }

    init {
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
        initializeViews()
        setupInitialState()
        // Configuración por defecto - puedes cambiarla después
        configureTickerSpaces(210f, 210f, 200f)
    }

    private fun configureTickerSpaces(leftAppear: Float, rightAppear: Float, gapBetweenCycles: Float) {
        leftAppearPx = leftAppear
        rightAppearPx = rightAppear
        gapBetweenCyclesPx = gapBetweenCycles

        // Aplicar márgenes independientes al container
        tickerContainer?.let { container ->
            val layoutParams = container.layoutParams as? ViewGroup.MarginLayoutParams
            layoutParams?.let {
                it.leftMargin = leftAppear.toInt()
                it.rightMargin = rightAppear.toInt()
                container.layoutParams = it

                Log.d(TAG, "Container margins applied - left: ${leftAppear.toInt()}, right: ${rightAppear.toInt()}")
            }
        }
    }

    private fun initializeViews() {
        val fragment = fragmentRef.get()
        tickerContainer = fragment?.view?.findViewById(R.id.ticker_container)
        tickerTextView = fragment?.view?.findViewById(R.id.ticker_text)
        tickerBackground = fragment?.view?.findViewById(R.id.ticker_background_image)

        if (tickerContainer == null || tickerTextView == null) {
            Log.e(TAG, "Ticker views not found")
            return
        }

        //tryMeasureViews(0)
    }

    private fun tryMeasureViews(retryCount: Int) {
        val container = tickerContainer ?: return
        if (container.width > 0) {
            isViewsMeasured = true
            Log.d(TAG, "Views measured - container width: ${container.width}")
            if (isTickerVisible()) {
                startTickerAnimation()
            }
        } else if (retryCount < MAX_MEASUREMENT_RETRIES) {
            Log.w(TAG, "Container width is 0, retrying measurement ($retryCount/$MAX_MEASUREMENT_RETRIES)")
            container.postDelayed({
                tryMeasureViews(retryCount + 1)
            }, MEASUREMENT_RETRY_DELAY)
        } else {
            Log.e(TAG, "Failed to measure container width after $MAX_MEASUREMENT_RETRIES retries")
            isViewsMeasured = true
        }
    }

    private fun setupInitialState() {
        if (tickerList.isNotEmpty()) setCurrentTicker()
    }

    override fun resizeTo(targetSize: VideoSize) {
        if (tickerList.any { it.isValid() }) {
            super.resizeTo(targetSize)
            Log.w(TAG, "Se encontró ticker válido")
            when (targetSize) {
                VideoSize.SMALL_SIZE -> {
                    // Avanza al siguiente ticker cada vez que se redimensiona
                    advanceToNextValidTicker()
                    // Mostrar inmediatamente
                    tickerContainer?.visibility = View.VISIBLE
                    // Esperar 2 frames para que las vistas se midan correctamente después del resize
                    tickerContainer?.post {
                        tickerContainer?.post {
                            isViewsMeasured = true
                            startTickerAnimation()
                        }
                    }
                }
                else -> hideTicker()
            }
        }
        else {
            Log.w(TAG, "Ningún ticket es válido, ocultando ticker, no se mostrarán más hasta que haya uno válido")
            hideTicker()
        }
    }

    private fun advanceToNextValidTicker() {
        if (tickerList.isEmpty()) return

        var attempts = 0

        do {
            currentTickerIndex = (currentTickerIndex + 1) % tickerList.size
            attempts++

            // Si hemos dado una vuelta completa sin encontrar ticker válido
            if (attempts >= tickerList.size) {
                // No hay tickers válidos, podrías manejarlo como prefieras:
                // Opción 1: Quedarse con el último index
                // Opción 2: Resetear a -1 o algún valor especial
                // Opción 3: Usar el primer ticker aunque no sea válido
                return
            }

        } while (!tickerList[currentTickerIndex].isValid())

        setCurrentTicker()
    }

    private fun setCurrentTicker() {
        if (tickerList.isEmpty()) return
        _ticker = tickerList[currentTickerIndex]
        currentTextIndex = 0 // Resetear índice de texto al cambiar ticker
        setCurrentTickerText()
        setTickerImageRemote()
    }

    private fun setCurrentTickerText() {
        val textos = _ticker?.textos ?: return
        if (textos.isEmpty()) return

        val currentText = textos[currentTextIndex]
        setTickerText(currentText)
    }

    private fun advanceToNextText() {
        val textos = _ticker?.textos ?: return
        if (textos.isEmpty()) return

        currentTextIndex = (currentTextIndex + 1) % textos.size
        setCurrentTickerText()
    }

    private fun setTickerText(text: String?) {
        tickerTextView?.apply {
            this.text = text ?: ""
            isSingleLine = true
            ellipsize = null
            setTextSize(TypedValue.COMPLEX_UNIT_SP, TEXT_SIZE_SP)

            // Forzar que el TextView tenga el ancho necesario para todo el texto
            measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )

            // Establecer el ancho del TextView al ancho medido
            layoutParams = layoutParams?.apply {
                width = measuredWidth
            }
        }
    }

    private fun setTickerImageRemote() {
        val fragment = fragmentRef.get() ?: return
        val imageUrl = _ticker?.fondo ?: return
        if (fragment.isAdded) {
            Glide.with(fragment)
                .load(imageUrl)
                .apply(RequestOptions().centerCrop())
                .into(tickerBackground ?: return)
        }
    }

    private fun showTicker() {
        tickerContainer?.visibility = View.VISIBLE
        // Solo delay para animaciones internas, no para resize
        tickerContainer?.postDelayed({
            if (isViewsMeasured) {
                startTickerAnimation()
            } else {
                Log.w(TAG, "Show ticker: waiting for views to be measured")
                tryMeasureViews(0)
            }
        }, 100) // Delay reducido
    }

    private fun hideTicker() {
        stopTickerAnimation()
        tickerContainer?.visibility = View.GONE
    }

    private fun startTickerAnimation() {
        stopTickerAnimation()
        val tickerText = tickerTextView ?: return
        val container = tickerContainer ?: return

        // Avanzar al siguiente texto del ticker actual antes de cada animación
        advanceToNextText()

        val containerWidth = container.width.toFloat()

        // Si el container no tiene ancho válido, reintentar
        if (containerWidth <= 0) {
            Log.w(TAG, "Container width is $containerWidth, retrying...")
            container.post { startTickerAnimation() }
            return
        }

        val textWidth = calculateTextWidth(tickerText)

        Log.d(TAG, "Starting animation - containerWidth: $containerWidth, textWidth: $textWidth, textIndex: $currentTextIndex")

        // Usar el ancho del container (ya modificado por configureTickerSpaces)
        val startPosition = containerWidth
        val endPosition = -textWidth
        val totalDistance = startPosition - endPosition

        // Colocar el texto en la posición inicial
        tickerText.translationX = startPosition
        tickerText.visibility = View.VISIBLE

        Log.d(TAG, "Animation positions - start: $startPosition, end: $endPosition, distance: $totalDistance")

        // Duración basada en la distancia real recorrida
        val duration = (totalDistance * ANIMATION_SPEED_FACTOR).toLong().coerceIn(
            MIN_ANIMATION_DURATION, MAX_ANIMATION_DURATION
        )

        createAnimation(tickerText, container, startPosition, endPosition, duration)
    }

    private fun createAnimation(
        tickerText: TextView,
        container: FrameLayout,
        start: Float,
        end: Float,
        duration: Long
    ) {
        tickerAnimator = ValueAnimator.ofFloat(start, end).apply {
            this.duration = duration
            interpolator = LinearInterpolator()

            addUpdateListener {
                if (tickerText.parent != null) {
                    tickerText.translationX = it.animatedValue as Float
                }
            }

            doOnEnd {
                tickerJob?.cancel()
                tickerJob = scope.launch {
                    tickerText.visibility = View.INVISIBLE
                    // La pausa es fija, independiente de la longitud del texto.
                    val pauseDuration = PAUSE_BETWEEN_CYCLES + (gapBetweenCyclesPx / ANIMATION_SPEED_FACTOR).toLong()
                    Log.d(TAG, "Pausing for $pauseDuration ms")
                    delay(pauseDuration)
                    if (isTickerVisible()) {
                        tickerText.translationX = start
                        tickerText.visibility = View.VISIBLE
                        // ✅ Sin avance automático - solo repite el mismo ticker
                        startTickerAnimation()
                    }
                }
            }
            start()
        }
    }

    private fun calculateTextWidth(textView: TextView): Float {
        // Para textos muy largos, calculamos directamente con Paint
        paint.textSize = textView.textSize
        paint.typeface = textView.typeface
        val paintWidth = paint.measureText(textView.text.toString())

        Log.d(TAG, "Text: '${textView.text}', paintWidth: $paintWidth")

        // Para el texto específico, asegurar ancho suficiente
        return paintWidth + TEXT_WIDTH_MARGIN
    }

    private fun stopTickerAnimation() {
        tickerJob?.cancel()
        tickerAnimator?.cancel()
        tickerAnimator?.removeAllListeners()
        tickerAnimator?.removeAllUpdateListeners()
        tickerAnimator = null
    }

    fun replaceTickers(newTickerList: List<Ticker>) {
        tickerList = newTickerList
        currentTickerIndex = 0
        if (tickerList.isNotEmpty()) {
            setCurrentTicker()
        } else {
            hideTicker()
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    override fun release() {
        stopTickerAnimation()
        tickerContainer = null
        tickerTextView = null
        tickerBackground = null
        _ticker = null
        scope.cancel()
        super.release()
    }

    fun getCurrentTicker(): Ticker? = _ticker
    fun getTickerCount(): Int = tickerList.size
    fun isTickerVisible(): Boolean = tickerContainer?.visibility == View.VISIBLE
}

