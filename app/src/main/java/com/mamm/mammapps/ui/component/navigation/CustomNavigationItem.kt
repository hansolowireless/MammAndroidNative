package com.mamm.mammapps.ui.component.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults

@Composable
fun CustomTVNavigationItem(
    icon: @Composable () -> Unit,
    label: String,
    selected: Boolean,
    parentIsFocused: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    ListItem(
        selected = false, // Manejamos la selección manualmente con colores
        onClick = onClick,
        headlineContent = {
            AnimatedVisibility(
                visible = parentIsFocused,
                enter = slideInHorizontally(initialOffsetX = { -it / 2 }) + fadeIn(),
                exit = slideOutHorizontally(targetOffsetX = { -it / 2 }) + fadeOut()
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(24.dp),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }
        },
        colors = ListItemDefaults.colors(
            containerColor = when {
                selected -> MaterialTheme.colorScheme.secondaryContainer
                else -> Color.Transparent
            },
            contentColor = when {
                isFocused -> MaterialTheme.colorScheme.primary
                selected -> MaterialTheme.colorScheme.onSecondaryContainer
                else -> MaterialTheme.colorScheme.onSurface
            }
        ),
        shape = ListItemDefaults.shape(shape = RectangleShape),
        modifier = modifier,
        interactionSource = interactionSource
    )
}
