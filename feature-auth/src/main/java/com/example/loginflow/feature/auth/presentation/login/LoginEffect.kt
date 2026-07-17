package com.example.loginflow.feature.auth.presentation.login

sealed interface LoginEffect {
    data object ShowBiometricDialog : LoginEffect
    data object NavigateToProfile : LoginEffect
}
