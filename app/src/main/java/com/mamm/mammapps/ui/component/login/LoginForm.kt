package com.mamm.mammapps.ui.component.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.mamm.mammapps.R
import com.mamm.mammapps.ui.component.LocalIsTV
import com.mamm.mammapps.ui.component.common.PrimaryButton
import com.mamm.mammapps.ui.component.common.TextInput
import com.mamm.mammapps.ui.theme.Dimensions
import com.mamm.mammapps.ui.theme.TextPrimary


@Composable
fun LoginForm(
    onLogin: (email: String, password: String) -> Unit,
    showInitialText: Boolean = true,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isFormValid = email.isNotBlank() && password.isNotBlank()

    // 1. Crear los FocusRequesters
    val (emailFocusRequester, passwordFocusRequester) = remember { FocusRequester.createRefs() }

    // 2. Solicitar el foco para el email cuando el composable aparece
    LaunchedEffect(Unit) {
        emailFocusRequester.requestFocus()
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(if (LocalIsTV.current) Dimensions.paddingLarge else Dimensions.paddingSmall),
        modifier = Modifier.fillMaxWidth()
    ) {
        if (showInitialText) {
            Text(
                text = stringResource(R.string.type_credentials),
                style = MaterialTheme.typography.headlineMedium,
                color = TextPrimary
            )
        }
        TextInput(
            label = stringResource(R.string.email),
            value = email,
            onValueChange = { email = it },
            keyboardType = KeyboardType.Email,
            modifier = Modifier.focusRequester(emailFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(
                onNext = { passwordFocusRequester.requestFocus() }
            )
        )
        TextInput(
            label = stringResource(R.string.password),
            value = password,
            onValueChange = { password = it },
            keyboardType = KeyboardType.Password,
            isPassword = true,
            modifier = Modifier.focusRequester(passwordFocusRequester),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = { if(isFormValid) onLogin(email, password) }
            )
        )
        PrimaryButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.login),
            height = 55.dp,
            onClick = { onLogin(email, password) },
            enabled = isFormValid
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
