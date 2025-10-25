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
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.mapper.toDateSelectorResId
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
                        text = stringResource(id = date.toDateSelectorResId()),
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
    val dates = remember {
        listOf(
            LocalDate.now().minusDays(2),
            LocalDate.now().minusDays(1),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(2)
        )
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        dates.forEach { date ->
            ListItem(
                colors = ListItemDefaults.colors(
                    containerColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                    selectedContainerColor = Color.Transparent
                ),
                headlineContent = {
                    Text(
                        text = stringResource(id = date.toDateSelectorResId()),
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
