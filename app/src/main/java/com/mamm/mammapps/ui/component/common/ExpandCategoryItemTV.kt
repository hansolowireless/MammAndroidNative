package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Glow
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import androidx.tv.material3.SurfaceDefaults
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.extension.onTap
import com.mamm.mammapps.ui.theme.ContentEntityColor
import com.mamm.mammapps.ui.theme.Dimensions

@Composable
fun ExpandCategoryItemTV (
    modifier : Modifier = Modifier,
    onClick: () -> Unit,
    onFocus: () -> Unit = {}
) {

    var isFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier
            .graphicsLayer {
                scaleX = if (isFocused) 0.98f else 0.92f
                scaleY = if (isFocused) 0.98f else 0.92f
            }
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
                if (isFocused) onFocus()
            }
            .focusable(enabled = LocalIsTV.current)
            .onTap { onClick() }
            .clickable {
                onClick()
            },
        glow = Glow(
            elevationColor = if (isFocused) ContentEntityColor.glow else Color.Transparent,
            elevation = 12.dp
        ),
        shape = RoundedCornerShape(Dimensions.cornerRadius),
        colors = SurfaceDefaults.colors(containerColor = Color.DarkGray)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(Dimensions.cornerRadius))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "+",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 60.sp),
                color = Color.White
            )
        }
    }
}