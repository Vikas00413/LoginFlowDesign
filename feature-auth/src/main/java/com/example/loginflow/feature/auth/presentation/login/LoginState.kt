package com.example.loginflow.feature.auth.presentation.login

data class LoginState(
    val username: String = "emilys",
    val password: String = "emilyspass",
    val isLoading: Boolean = false,
    val error: String? = null
)
