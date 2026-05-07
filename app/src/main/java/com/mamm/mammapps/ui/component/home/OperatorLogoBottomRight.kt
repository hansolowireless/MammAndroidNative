package com.mamm.mammapps.ui.component.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.OperatorLogoImage
import com.mamm.mammapps.ui.theme.Dimensions

/**
 * Un Composable que muestra una pantalla de carga con una imagen de fondo
 * y el logo del operador en la esquina inferior derecha.
 *
 * @param modifier El modificador a aplicar al Box contenedor.
 * @param logoUrl La URL del logo del operador a mostrar. Puede ser nulo.
 */
@Composable
fun OperatorLogoBottomRight(
    modifier: Modifier = Modifier,
    logoUrl: String?
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.tv_home_logo_shadow),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Fit
        )

        logoUrl?.let {
            OperatorLogoImage(
                logoUrl = it,
                modifier = Modifier
                    .width(150.dp)
                    .align(Alignment.BottomEnd)
                    .padding(Dimensions.paddingMedium)
            )
        }
    }
}