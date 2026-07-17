package com.example.loginflow.core.database.security

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabasePassphraseManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "db_passphrase_prefs",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun getPassphrase(): ByteArray {
        val existing = prefs.getString(KEY_PASSPHRASE, null)
        if (existing != null) {
            return existing.toByteArray(Charsets.UTF_8)
        }
        val generated = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val encoded = generated.joinToString("") { "%02x".format(it) }
        prefs.edit().putString(KEY_PASSPHRASE, encoded).apply()
        return encoded.toByteArray(Charsets.UTF_8)
    }

    companion object {
        private const val KEY_PASSPHRASE = "room_passphrase"
    }
}
