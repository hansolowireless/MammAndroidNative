package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.theme.Primary

@Composable
fun PrimaryButton(
    modifier: Modifier = Modifier,
    text: String,
    icon: @Composable (() -> Unit)? = null,
    onClick: () -> Unit,
) {
    Button (
        onClick = onClick,
        shape = ButtonDefaults.shape(RectangleShape),
        modifier = modifier
            .fillMaxWidth()
            .height(if (LocalIsTV.current) 80.dp else 48.dp),
        colors = ButtonDefaults.colors(
            containerColor = Primary,
            contentColor = Color.Black
        )
    ) {
        if (icon != null) {
            icon()
        }
        Text(text)
    }
}