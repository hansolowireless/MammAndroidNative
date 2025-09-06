package com.mamm.mammapps.ui.component.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.common.TextInput


@Composable
fun LoginForm(
    onLogin: (email: String, password: String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        verticalArrangement = Arrangement.spacedBy(if (LocalIsTV.current) 32.dp else 16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        TextInput(
            label = "Email",
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email
        )
        TextInput(
            label = "Password",
            value = password,
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password,
            isPassword = true
        )
        PrimaryButton(
            text = "Login",
            onClick = { onLogin(email, password) }
        )
    }
}

@Preview(name = "Login Form - TV")
@Composable
fun LoginFormTVPreview() {
    LoginForm(
        onLogin = { email, password ->
            // Preview action - no actual login
        }
    )
}