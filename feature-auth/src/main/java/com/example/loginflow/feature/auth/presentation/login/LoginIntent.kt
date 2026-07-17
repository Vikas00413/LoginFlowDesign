package com.example.loginflow.feature.auth.presentation.login

import com.example.loginflow.core.auth.model.AuthProvider

sealed interface LoginIntent {
    data class UsernameChanged(val value: String) : LoginIntent
    data class PasswordChanged(val value: String) : LoginIntent
    data object LoginClicked : LoginIntent
    data class SocialLogin(val provider: AuthProvider) : LoginIntent
    data class BiometricChoice(val enable: Boolean) : LoginIntent
    data object SkipBiometric : LoginIntent
}
