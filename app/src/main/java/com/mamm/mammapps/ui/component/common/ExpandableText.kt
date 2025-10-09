package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.theme.Dimensions

/**
 * Un componente de texto que se colapsa después de un número determinado de líneas
 * y muestra un botón "Mostrar más" para expandirse.
 *
 * @param modifier El modificador a aplicar al Column que contiene el texto.
 * @param text El texto a mostrar.
 * @param style El estilo del texto principal.
 * @param color El color del texto principal.
 * @param collapsedMaxLines El número de líneas a mostrar cuando el texto está colapsado.
 * @param textAlign La alineación del texto principal.
 */
@Composable
fun ExpandableText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    collapsedMaxLines: Int = 4,
    textAlign: TextAlign = TextAlign.Start,
) {
    var isExpanded by remember { mutableStateOf(false) }
    var isOverflowing by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Text(
            text = text,
            style = style,
            color = color,
            textAlign = textAlign,
            maxLines = if (isExpanded) Int.MAX_VALUE else collapsedMaxLines,
            onTextLayout = { textLayoutResult ->
                // Solo actualiza si el estado cambia para evitar recomposiciones innecesarias
                if (textLayoutResult.hasVisualOverflow != isOverflowing) {
                    isOverflowing = textLayoutResult.hasVisualOverflow
                }
            }
        )

        if (isOverflowing || isExpanded) {
            Spacer(modifier = Modifier.height(Dimensions.paddingXSmall))
            Text(
                text = if (isExpanded) {
                    stringResource(R.string.show_less)
                } else {
                    stringResource(R.string.show_more)
                },
                style = MaterialTheme.typography.labelLarge.copy(color = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        isExpanded = !isExpanded
                    }
                    .align(Alignment.End)
            )
        }
    }
}
