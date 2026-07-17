package com.example.loginflow.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.loginflow.feature.auth.presentation.biometric.BiometricIntent
import com.example.loginflow.feature.auth.presentation.biometric.BiometricViewModel

@Composable
fun BiometricRoute(
    activity: FragmentActivity,
    viewModel: BiometricViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value

    LaunchedEffect(Unit) {
        BiometricPromptHandler.show(
            activity = activity,
            onSuccess = { viewModel.onIntent(BiometricIntent.BiometricSuccess) },
            onError = { viewModel.onIntent(BiometricIntent.BiometricError) }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Unlock with Biometric", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        state.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            BiometricPromptHandler.show(
                activity = activity,
                onSuccess = { viewModel.onIntent(BiometricIntent.BiometricSuccess) },
                onError = { viewModel.onIntent(BiometricIntent.BiometricError) }
            )
        }) {
            Text("Try again")
        }
        TextButton(onClick = { viewModel.onIntent(BiometricIntent.BiometricError) }) {
            Text("Use password login")
        }
    }
}
