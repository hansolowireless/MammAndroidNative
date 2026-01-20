package com.mamm.mammapps.ui.model

import androidx.annotation.StringRes
import com.mamm.mammapps.R

enum class PlayButtonModeUI(@StringRes val resId: Int) {
    PLAY(R.string.play),
    CONTINUE(R.string.continue_watching);
}

