package com.example.loginflow.core.auth.model

data class User(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String,
    val provider: String = "email"
) {
    val displayName: String get() = "$firstName $lastName"
}

enum class AuthProvider {
    EMAIL, GOOGLE, FACEBOOK
}

sealed class AuthState {
    data object Unknown : AuthState()
    data object LoggedOut : AuthState()
    data class LoggedIn(val user: User) : AuthState()
}
