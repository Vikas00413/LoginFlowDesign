package com.example.loginflow.feature.auth.presentation.biometric

sealed interface BiometricIntent {
    data object BiometricSuccess : BiometricIntent
    data object BiometricError : BiometricIntent
}
