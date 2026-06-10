package com.mamm.mammapps.ui.component.diagnostic

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.domain.model.about.DownloadSpeedResult
import com.mamm.mammapps.ui.extension.formatMbps
import com.mamm.mammapps.ui.theme.TextPrimary

@Composable
fun DiagnosticResultsList(
    results: List<DownloadSpeedResult?>
) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        results.forEachIndexed { index, result ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.node).plus(index + 1),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextPrimary
                )

                if (result != null) {
                    Text(
                        text = "${result.speedMbps.formatMbps()} Mbps",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Green
                    )
                } else {
                    Text(
                        text = "Error",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
            }
        }
    }
}