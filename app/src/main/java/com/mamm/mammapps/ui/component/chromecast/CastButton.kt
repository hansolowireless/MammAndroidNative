package com.mamm.mammapps.ui.component.chromecast

import androidx.appcompat.view.ContextThemeWrapper
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.mediarouter.app.MediaRouteButton
import com.google.android.gms.cast.framework.CastButtonFactory
import com.mamm.mammapps.R

@Composable
fun CastButton() {
    AndroidView(
        factory = { context ->
            // 1. Envuelve el contexto con tu estilo personalizado
            val themedContext =
                ContextThemeWrapper(context, R.style.CustomMediaRouteButtonStyle)

            // 2. Crea el botón usando este nuevo contexto
            val mediaRouteButton = MediaRouteButton(themedContext)

            // 3. Configura el botón como siempre
            CastButtonFactory.setUpMediaRouteButton(themedContext, mediaRouteButton)

            mediaRouteButton
        }
    )
}

