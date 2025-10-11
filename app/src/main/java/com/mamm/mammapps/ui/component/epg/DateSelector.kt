package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.LocalIsTV
import java.time.LocalDate

@Composable
fun DateSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    if (LocalIsTV.current) {
        TvDateSelector(
            modifier = modifier,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    } else {
        MobileDateSelector(
            modifier = modifier,
            selectedDate = selectedDate,
            onDateSelected = onDateSelected
        )
    }
}

@Composable
private fun MobileDateSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    // 1. Crear la lista de pestañas en el orden correcto
    val tabs = remember {
        listOf(
            LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        )
    }

    // 2. Encontrar el índice de la pestaña seleccionada
    val selectedTabIndex = tabs.indexOf(selectedDate)

    // 3. Crear el TabRow
    TabRow(
        modifier = modifier,
        selectedTabIndex = selectedTabIndex,
        containerColor = Color.Transparent, // O el color que prefieras para el fondo de la barra
        contentColor = MaterialTheme.colorScheme.primary // Color del indicador y del texto seleccionado por defecto
    ) {
        // 4. Iterar sobre la lista de pestañas para crear cada Tab
        tabs.forEachIndexed { index, date ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onDateSelected(date) },
                text = {
                    Text(
                        text = stringResource(id = date.toStringResource()),
                        color = if (selectedTabIndex == index) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun TvDateSelector(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datesMap = remember {
        mapOf(
            LocalDate.now().minusDays(2) to R.string.day_before_yesterday,
            LocalDate.now().minusDays(1) to R.string.yesterday,
            LocalDate.now() to R.string.today,
            LocalDate.now().plusDays(1) to R.string.tomorrow,
            LocalDate.now().plusDays(2) to R.string.day_after_tomorrow
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        datesMap.forEach { (date, stringResId) ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedContainerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = stringResource(id = stringResId),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                },
                trailingContent = {
                    if (date == selectedDate) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                onClick = {
                    onDateSelected(date)
                },
                selected = date == selectedDate
            )
        }
    }
}

// Función de extensión para mantener la lógica de mapeo en un solo lugar
private fun LocalDate.toStringResource(): Int {
    val now = LocalDate.now()
    return when {
        this == now.minusDays(2) -> R.string.day_before_yesterday
        this == now.minusDays(1) -> R.string.yesterday
        this == now -> R.string.today
        this == now.plusDays(1) -> R.string.tomorrow
        this == now.plusDays(2) -> R.string.day_after_tomorrow
        else -> R.string.today // Un fallback por si acaso
    }
}
