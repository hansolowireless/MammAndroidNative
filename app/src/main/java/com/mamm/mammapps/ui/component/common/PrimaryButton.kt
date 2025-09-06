package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.theme.Primary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
) {
    Button(
        shape = RectangleShape,
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(if (LocalIsTV.current) 80.dp else 48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Primary,
            contentColor = Color.Black
        )
    ) {
        Text(text)
    }
}