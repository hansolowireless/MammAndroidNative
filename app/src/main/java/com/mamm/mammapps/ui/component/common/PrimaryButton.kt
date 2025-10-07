package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.theme.ButtonColor

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    height: Dp = if (LocalIsTV.current) 40.dp else 48.dp,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
    onRegainFocus: (() -> Unit)? = null,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    LaunchedEffect(isFocused) {
        if (isFocused) onRegainFocus?.invoke()
    }

    Button(
        onClick = onClick,
        shape = ButtonDefaults.shape(RectangleShape),
        modifier = modifier
            .fillMaxWidth()
            .height(height),
        glow = ButtonDefaults.glow(),
        colors = ButtonDefaults.colors(
            containerColor = ButtonColor.background,
            focusedContainerColor = ButtonColor.background,
            contentColor = ButtonColor.unfocusedContent,
            focusedContentColor = ButtonColor.focusedContent
        ),
        interactionSource = interactionSource // Pasar el interactionSource
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
            }
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Preview
@Composable
fun PrimaryButtonPreview() {
    MaterialTheme {
        PrimaryButton(text = "Play", onClick = {})
    }
}