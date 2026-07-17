package com.example.loginflow.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loginflow.feature.auth.presentation.login.LoginEffect
import com.example.loginflow.feature.auth.presentation.login.LoginIntent
import com.example.loginflow.feature.auth.presentation.login.LoginState
import com.example.loginflow.feature.auth.presentation.login.LoginViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun LoginRoute(
    onGoogleSignIn: () -> Unit,
    onFacebookSignIn: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showBiometricDialog by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                LoginEffect.ShowBiometricDialog -> showBiometricDialog = true
                LoginEffect.NavigateToProfile -> Unit
            }
        }
    }

    if (showBiometricDialog) {
        AlertDialog(
            onDismissRequest = {
                showBiometricDialog = false
                viewModel.onIntent(LoginIntent.SkipBiometric)
            },
            title = { Text("Enable Biometric?") },
            text = { Text("Use fingerprint or face for faster unlock next time.") },
            confirmButton = {
                TextButton(onClick = {
                    showBiometricDialog = false
                    viewModel.onIntent(LoginIntent.BiometricChoice(enable = true))
                }) {
                    Text("Enable")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showBiometricDialog = false
                    viewModel.onIntent(LoginIntent.SkipBiometric)
                }) {
                    Text("Not now")
                }
            }
        )
    }

    LoginScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onGoogleClick = onGoogleSignIn,
        onFacebookClick = onFacebookSignIn
    )
}

@Composable
fun LoginScreen(
    state: LoginState,
    onIntent: (LoginIntent) -> Unit,
    onGoogleClick: () -> Unit,
    onFacebookClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = state.username,
            onValueChange = { onIntent(LoginIntent.UsernameChanged(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = { onIntent(LoginIntent.PasswordChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        state.error?.let { error ->
            Spacer(Modifier.height(8.dp))
            Text(error, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onIntent(LoginIntent.LoginClicked) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("Login")
            }
        }

        Spacer(Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(onClick = onGoogleClick, enabled = !state.isLoading) {
                Text("Google")
            }
            OutlinedButton(onClick = onFacebookClick, enabled = !state.isLoading) {
                Text("Facebook")
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(
            "Demo: emilys / emilyspass",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
