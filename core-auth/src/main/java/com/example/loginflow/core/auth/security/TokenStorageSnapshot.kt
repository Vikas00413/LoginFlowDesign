package com.example.loginflow.core.auth.security

data class TokenStorageSnapshot(
    val prefsName: String,
    val hasAccessToken: Boolean,
    val hasRefreshToken: Boolean,
    val accessTokenPreview: String?,
    val refreshTokenPreview: String?,
    val accessTokenExpiresAt: Long,
    val isAccessTokenValid: Boolean,
    val biometricEnabled: Boolean
)
