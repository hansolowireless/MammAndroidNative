package com.mamm.mammapps.ui.component.player.custompreviewbar

import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.github.rubensousa.previewseekbar.PreviewBar
import com.github.rubensousa.previewseekbar.PreviewBar.OnPreviewVisibilityListener
import com.github.rubensousa.previewseekbar.PreviewDelegate
import com.github.rubensousa.previewseekbar.PreviewLoader
import com.github.rubensousa.previewseekbar.animator.PreviewAnimator
import com.github.rubensousa.previewseekbar.exoplayer.R
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.TimeBar

/**
 * A [DefaultTimeBar] that mimics the behavior of a [PreviewSeekBar].
 *
 *
 * When the user scrubs this TimeBar, a preview will appear above the scrubber.
 */

/* The Default bar that comes with Android was modified to accommodate the timeshift function*/

class CustomPreviewBar(context: Context, attrs: AttributeSet?) :
    CustomTimeBar(context, attrs), PreviewBar {
    private val delegate: PreviewDelegate
    private var scrubProgress = 0
    private var duration = 0
    private var scrubberColor: Int
    private val previewId: Int
    private var scrubberPadding = 0

    init {
        var typedArray = context.theme.obtainStyledAttributes(
            attrs,
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar, 0, 0
        )
        scrubberColor = typedArray.getInt(
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_color,
            DEFAULT_SCRUBBER_COLOR
        )

        val scrubberDrawable = typedArray.getDrawable(
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_drawable
        )

        val scrubberEnabledSize = typedArray.getDimensionPixelSize(
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_enabled_size,
            dpToPx(
                context.resources.displayMetrics,
                DEFAULT_SCRUBBER_ENABLED_SIZE_DP
            )
        )
        val scrubberDisabledSize = typedArray.getDimensionPixelSize(
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_disabled_size,
            dpToPx(
                context.resources.displayMetrics,
                DEFAULT_SCRUBBER_DISABLED_SIZE_DP
            )
        )
        val scrubberDraggedSize = typedArray.getDimensionPixelSize(
            com.google.android.exoplayer2.ui.R.styleable.DefaultTimeBar_scrubber_dragged_size,
            dpToPx(
                context.resources.displayMetrics,
                DEFAULT_SCRUBBER_DRAGGED_SIZE_DP
            )
        )

        // Calculate the scrubber padding based on the maximum size the scrubber can have
        scrubberPadding = if (scrubberDrawable != null) {
            (scrubberDrawable.minimumWidth + 1) / 2
        } else {
            (Math.max(
                scrubberDisabledSize,
                Math.max(scrubberEnabledSize, scrubberDraggedSize)
            ) + 1) / 2
        }
        typedArray.recycle()
        typedArray = context.theme.obtainStyledAttributes(
            attrs, R.styleable.PreviewTimeBar, 0, 0
        )
        previewId = typedArray.getResourceId(
            R.styleable.PreviewTimeBar_previewFrameLayout, NO_ID
        )
        delegate = PreviewDelegate(this)
        delegate.isPreviewEnabled = isEnabled
        delegate.setAnimationEnabled(
            typedArray.getBoolean(
                R.styleable.PreviewTimeBar_previewAnimationEnabled, true
            )
        )
        delegate.isPreviewEnabled = typedArray.getBoolean(
            R.styleable.PreviewTimeBar_previewEnabled, true
        )
        delegate.setAutoHidePreview(
            typedArray.getBoolean(
                R.styleable.PreviewTimeBar_previewAutoHide, true
            )
        )
        typedArray.recycle()
        addListener(TimeBarDefaultOnScrubListener())
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (!delegate.isPreviewViewAttached && !isInEditMode) {
            val previewView = PreviewDelegate.findPreviewView(
                (parent as ViewGroup), previewId
            )
            if (previewView != null) {
                delegate.attachPreviewView(previewView)
            }
        }
    }

    override fun setPreviewThumbTint(color: Int) {
        setScrubberColor(color)
        scrubberColor = color
    }

    override fun setPreviewThumbTintResource(colorResource: Int) {
        setPreviewThumbTint(ContextCompat.getColor(context, colorResource))
    }

    override fun setPreviewLoader(previewLoader: PreviewLoader?) {
        delegate.setPreviewLoader(previewLoader)
    }

    override fun attachPreviewView(previewView: FrameLayout) {
        delegate.attachPreviewView(previewView)
    }

    override fun setDuration(duration: Long) {
        super.setDuration(duration)
        val newDuration = duration.toInt()
        if (newDuration != this.duration) {
            this.duration = newDuration
            delegate.updateProgress(progress, newDuration)
        }
    }

    override fun setPosition(position: Long) {
        super.setPosition(position)
        var newPosition = position.toInt()
        if (newPosition != scrubProgress) {
            scrubProgress = newPosition
            delegate.updateProgress(newPosition, duration)
        }
    }

    override fun isShowingPreview(): Boolean {
        return delegate.isShowingPreview
    }

    override fun isPreviewEnabled(): Boolean {
        return delegate.isPreviewEnabled
    }

    override fun setPreviewEnabled(enabled: Boolean) {
        delegate.isPreviewEnabled = enabled
    }

    override fun showPreview() {
        delegate.show()
    }

    override fun hidePreview() {
        delegate.hide()
    }

    override fun setAutoHidePreview(autoHide: Boolean) {
        delegate.setAutoHidePreview(autoHide)
    }

    override fun getProgress(): Int {
        return scrubProgress
    }

    override fun getMax(): Int {
        return duration
    }

    override fun getThumbOffset(): Int {
        return scrubberPadding
    }

    override fun getScrubberColor(): Int {
        return scrubberColor
    }

    override fun setScrubberColor(scrubberColor: Int) {
        super.setScrubberColor(scrubberColor)
        this.scrubberColor = scrubberColor
    }

    override fun addOnScrubListener(listener: PreviewBar.OnScrubListener) {
        delegate.addOnScrubListener(listener)
    }

    override fun removeOnScrubListener(listener: PreviewBar.OnScrubListener) {
        delegate.removeOnScrubListener(listener)
    }

    override fun addOnPreviewVisibilityListener(listener: OnPreviewVisibilityListener) {
        delegate.addOnPreviewVisibilityListener(listener)
    }

    override fun removeOnPreviewVisibilityListener(listener: OnPreviewVisibilityListener) {
        delegate.removeOnPreviewVisibilityListener(listener)
    }

    override fun setPreviewAnimator(animator: PreviewAnimator) {
        delegate.setAnimator(animator)
    }

    override fun setPreviewAnimationEnabled(enable: Boolean) {
        delegate.setAnimationEnabled(enable)
    }

    private fun dpToPx(displayMetrics: DisplayMetrics, dps: Int): Int {
        return (dps * displayMetrics.density + 0.5f).toInt()
    }

    /**
     * Listens for scrub events to show, hide or move the preview frame
     */
    private inner class TimeBarDefaultOnScrubListener : TimeBar.OnScrubListener {
        override fun onScrubStart(timeBar: TimeBar, position: Long) {
            scrubProgress = position.toInt()
            delegate.onScrubStart()
        }

        override fun onScrubMove(timeBar: TimeBar, position: Long) {
            scrubProgress = position.toInt()
            delegate.onScrubMove(position.toInt(), true)
        }

        override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
            scrubProgress = position.toInt()
            delegate.onScrubStop()
        }
    }
}