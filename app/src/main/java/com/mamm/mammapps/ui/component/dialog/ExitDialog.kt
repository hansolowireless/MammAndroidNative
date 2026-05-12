package com.mamm.mammapps.ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.theme.MammAppsTheme

@Composable
fun ExitDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(text = stringResource(R.string.do_you_want_exit))
        },
        text = {
            Text(text = stringResource(R.string.do_you_want_exit))
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(stringResource(R.string.yes))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(stringResource(R.string.no))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun ExitDialogPreview() {
    MammAppsTheme {
       ExitDialog(
           onDismissRequest = {},
           onConfirmation = {}
       )
   }
}
