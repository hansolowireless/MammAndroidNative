package com.mamm.mammapps.ui.component.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun deviceAdaptivePadding(isTV: Boolean): PaddingValues {
    return if (isTV) {
        PaddingValues(horizontal = 100.dp, vertical = 50.dp)
    } else {
        PaddingValues(16.dp)
    }
}