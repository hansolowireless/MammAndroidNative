package com.mamm.mammapps.ui.model.uistate

import com.google.android.gms.cast.framework.CastSession

sealed class CastState {
    object NoSession : CastState()
    data class SessionStarted(val session: CastSession) : CastState()
    data class SessionEnded(val error: Int?) : CastState()
}