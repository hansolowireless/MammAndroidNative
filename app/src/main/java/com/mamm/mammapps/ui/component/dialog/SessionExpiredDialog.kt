package com.mamm.mammapps.ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.PrimaryButton

@Composable
fun SessionExpiredDialog(
    modifier: Modifier = Modifier,
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { /* No dismiss */ },
        title = {
            Text(text = stringResource(id = R.string.error_session_expired))
        },
        confirmButton = {
            PrimaryButton(
                text = stringResource(id = R.string.ok),
                onClick = {
                    onConfirm()
                }
            )
        }
    )
}
