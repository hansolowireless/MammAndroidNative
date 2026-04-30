package com.mamm.mammapps.ui.manager.videoresize

import android.util.Log
import android.view.View
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.player.Ticker
import java.lang.ref.WeakReference

class VideoResizeManagerWithTicker(
    fragment: Fragment,
    frameLayoutId: Int,
    private var tickerList: List<Ticker>,
    private val onTickerShown: () -> Unit = {}
) : VideoResizeManager(fragment, frameLayoutId), DefaultLifecycleObserver {

    private val fragmentRef = WeakReference(fragment)
    private var currentTickerIndex = 0


    private var tickerWebView: WebView? = null

    private var _ticker: Ticker? = null

    companion object {
        private const val TAG = "VTManager"
    }

    init {
        fragment.viewLifecycleOwner.lifecycle.addObserver(this)
        initializeViews()
    }

    private fun initializeViews() {
        val fragment = fragmentRef.get() ?: return

        tickerWebView = fragment.view?.findViewById(R.id.ticker_webview)

        configureWebView()
    }

    private fun configureWebView() {
        tickerWebView?.apply {
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.mediaPlaybackRequiresUserGesture = false
            setBackgroundColor(0)
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            isFocusable = false
            setOnTouchListener { _, _ -> true }
            visibility = View.GONE

            webChromeClient = object : android.webkit.WebChromeClient() {
                override fun getDefaultVideoPoster(): android.graphics.Bitmap? {
                    // Return a 1x1 transparent bitmap to hide the default play icon/poster
                    return android.graphics.Bitmap.createBitmap(1, 1, android.graphics.Bitmap.Config.ARGB_8888)
                }
            }
        }
    }

    override fun resizeTo(targetSize: VideoSize) {
        val hasValidTickers = tickerList.any { it.isValid() }
        Log.d(TAG, "resizeTo llamado con: $targetSize. ¿Tiene tickers válidos?: $hasValidTickers")

        val finalSize = if (targetSize == VideoSize.SMALL_SIZE && !hasValidTickers) {
            VideoSize.FULL_SIZE
        } else {
            targetSize
        }

        super.resizeTo(finalSize)

        if (finalSize == VideoSize.SMALL_SIZE) {
            showTicker()
        } else {
            hideTicker()
        }
    }

    /**
     * Actualiza la lista de tickers dinámicamente.
     */
    fun replaceTickers(newTickerList: List<Ticker>) {
        Log.d(TAG, "replaceTickers llamado con: $newTickerList")
        tickerList = newTickerList
        currentTickerIndex = 0

        if (tickerList.any { it.isValid() }) {
            // Si el ticker ya está visible, actualizamos el contenido inmediatamente
            if (currentSize == VideoSize.SMALL_SIZE) {
                showTicker()
            }
        } else {
            hideTicker()
        }
    }

    private fun showTicker() {
        tickerWebView?.visibility = View.VISIBLE
        advanceToNextValidTicker()
        loadRemoteTicker()
        onTickerShown()
    }

    private fun hideTicker() {
        tickerWebView?.visibility = View.GONE
        tickerWebView?.loadUrl("about:blank")
    }

    private fun advanceToNextValidTicker() {
        if (tickerList.isEmpty()) return

        // Buscamos el siguiente ticker válido (por si hay nulos o vacíos en la lista)
        var attempts = 0
        do {
            currentTickerIndex = (currentTickerIndex + 1) % tickerList.size
            attempts++
            if (attempts >= tickerList.size) break
        } while (!tickerList[currentTickerIndex].isValid())

        _ticker = tickerList[currentTickerIndex]


    }

    private fun loadRemoteTicker() {
        // Usamos tu URL fija o una del objeto ticker si la tuviera
        Log.d(TAG, "Cargando URL de ticker: ${_ticker?.htmlUrl}")

        _ticker?.htmlUrl?.let {
            tickerWebView?.loadUrl(it)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        release()
    }

    override fun stopAutoResize() {
        super.stopAutoResize()
        hideTicker()
    }

    override fun release() {
        super.release()
        tickerWebView?.apply {
            stopLoading()
            loadUrl("about:blank")
            destroy()
        }
        tickerWebView = null

    }
}