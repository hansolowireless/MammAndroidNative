package com.mamm.mammapps.ui.component.epg

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.theme.Dimensions
import java.time.LocalDate

@Composable
fun DatesColumn(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val datesMap = mapOf(
        LocalDate.now().minusDays(2) to R.string.day_before_yesterday,
        LocalDate.now().minusDays(1) to R.string.yesterday,
        LocalDate.now() to R.string.today,
        LocalDate.now().plusDays(1) to R.string.tomorrow,
        LocalDate.now().plusDays(2) to R.string.day_after_tomorrow
    )

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        datesMap.forEach { (date, stringResId) ->
            Card (
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.colors(
                    containerColor = if (date == selectedDate) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                onClick = {
                    onDateSelected(date)
                }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(Dimensions.paddingMedium),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(id = stringResId),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (date == selectedDate) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}