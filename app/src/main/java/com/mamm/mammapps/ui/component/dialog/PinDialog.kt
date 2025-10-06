package com.mamm.mammapps.ui.component.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.common.TextInput

@Composable
fun PinDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (pin: String) -> Unit
) {
    var pinValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.pin_dialog_title))
        },
        text = {
            Column {
                Text(text = stringResource(R.string.pin_dialog_message))
                Spacer(modifier = Modifier.height(16.dp))
                // Usamos tu componente TextInput ya existente
                TextInput(
                    value = pinValue,
                    onValueChange = { pinValue = it },
                    label = stringResource(R.string.pin_field_label),
                    keyboardType = KeyboardType.NumberPassword, // Teclado numérico
                    isPassword = true,                          // Oculta los números
                    imeAction = ImeAction.Done,
                    onDone = {
                        // Permite confirmar pulsando "Hecho" en el teclado
                        if (pinValue.isNotBlank()) {
                            onConfirm(pinValue)
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(pinValue)
                },
                // El botón solo se activa si se ha introducido texto
                enabled = pinValue.isNotBlank()
            ) {
                Text(stringResource(R.string.confirm_button_label))
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancel_button_label))
            }
        }
    )
}
