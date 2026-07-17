package com.example.loginflow.core.auth.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "auth_tokens",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String, expiresInMins: Int) {
        val expiresAt = System.currentTimeMillis() + expiresInMins * 60_000L
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putString(KEY_REFRESH, refreshToken)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun saveAccessToken(accessToken: String, expiresInMins: Int) {
        val expiresAt = System.currentTimeMillis() + expiresInMins * 60_000L
        prefs.edit()
            .putString(KEY_ACCESS, accessToken)
            .putLong(KEY_EXPIRES_AT, expiresAt)
            .apply()
    }

    fun saveRefreshToken(refreshToken: String) {
        prefs.edit().putString(KEY_REFRESH, refreshToken).apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS, null)

    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun isAccessTokenValid(): Boolean {
        val expiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L)
        return !getAccessToken().isNullOrBlank() &&
            System.currentTimeMillis() < (expiresAt - 30_000L)
    }

    fun hasRefreshToken(): Boolean = !getRefreshToken().isNullOrBlank()

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC, false)

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun readStorageSnapshot(): TokenStorageSnapshot {
        val accessToken = getAccessToken()
        val refreshToken = getRefreshToken()
        return TokenStorageSnapshot(
            prefsName = "auth_tokens (EncryptedSharedPreferences)",
            hasAccessToken = !accessToken.isNullOrBlank(),
            hasRefreshToken = hasRefreshToken(),
            accessTokenPreview = accessToken?.preview(),
            refreshTokenPreview = refreshToken?.preview(),
            accessTokenExpiresAt = prefs.getLong(KEY_EXPIRES_AT, 0L),
            isAccessTokenValid = isAccessTokenValid(),
            biometricEnabled = isBiometricEnabled()
        )
    }

    private fun String.preview(): String =
        if (length <= 12) this else "${take(8)}...${takeLast(4)}"

    companion object {
        private const val KEY_ACCESS = "access_token"
        private const val KEY_REFRESH = "refresh_token"
        private const val KEY_EXPIRES_AT = "access_expires_at"
        private const val KEY_BIOMETRIC = "biometric_enabled"
    }
}
