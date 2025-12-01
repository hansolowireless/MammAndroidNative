package com.mamm.mammapps.ui.component.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.mamm.mammapps.R

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
    // Para que la vista previa funcione, necesitarás añadir estos recursos de string
    // a un archivo de strings de prueba o a tus strings principales.
    // Ejemplo en res/values/strings.xml:
    // <string name="exit_dialog_title">Confirmación</string>
    // <string name="do_you_want_exit">¿Quieres salir?</string>
    // <string name="yes">Sí</string>
    // <string name="no">No</string>
    ExitDialog(
        onDismissRequest = {},
        onConfirmation = {}
    )
}
