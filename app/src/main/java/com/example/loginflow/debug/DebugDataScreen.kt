package com.example.loginflow.debug

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.DateFormat
import java.util.Date

@Composable
fun DebugDataRoute(
    viewModel: DebugDataViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Debug Data Viewer", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))
        Text(
            "Use this in debug builds. Android Studio App Inspection cannot read SQLCipher or EncryptedSharedPreferences directly.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(16.dp))

        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(12.dp))
            }

            SectionTitle("Room: users")
            if (state.users.isEmpty()) {
                MonoText("(empty)")
            } else {
                state.users.forEach { user ->
                    MonoText(
                        """
                        id=${user.id}
                        username=${user.username}
                        email=${user.email}
                        name=${user.firstName} ${user.lastName}
                        provider=${user.provider}
                        image=${user.imageUrl}
                        """.trimIndent()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("Room: sessions")
            if (state.sessions.isEmpty()) {
                MonoText("(empty)")
            } else {
                state.sessions.forEach { session ->
                    MonoText(
                        """
                        userId=${session.userId}
                        isLoggedIn=${session.isLoggedIn}
                        biometricEnabled=${session.biometricEnabled}
                        accessTokenExpiresAt=${session.accessTokenExpiresAt}
                        """.trimIndent()
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            SectionTitle("EncryptedSharedPreferences: tokens")
            state.tokenSnapshot?.let { snapshot ->
                val expiresLabel = if (snapshot.accessTokenExpiresAt > 0L) {
                    DateFormat.getDateTimeInstance().format(Date(snapshot.accessTokenExpiresAt))
                } else {
                    "n/a"
                }
                MonoText(
                    """
                    file=${snapshot.prefsName}
                    hasAccessToken=${snapshot.hasAccessToken}
                    hasRefreshToken=${snapshot.hasRefreshToken}
                    accessToken=${snapshot.accessTokenPreview ?: "null"}
                    refreshToken=${snapshot.refreshTokenPreview ?: "null"}
                    accessTokenValid=${snapshot.isAccessTokenValid}
                    accessTokenExpiresAt=$expiresLabel
                    biometricEnabled=${snapshot.biometricEnabled}
                    """.trimIndent()
                )
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = viewModel::refresh, modifier = Modifier.fillMaxWidth()) {
                Text("Refresh")
            }
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium)
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun MonoText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(12.dp))
}
