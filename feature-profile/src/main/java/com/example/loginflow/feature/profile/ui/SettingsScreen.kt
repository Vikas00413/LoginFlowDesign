package com.example.loginflow.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.loginflow.feature.profile.presentation.profile.ProfileIntent
import com.example.loginflow.feature.profile.presentation.profile.ProfileViewModel

@Composable
fun SettingsRoute(
    onOpenDebugData: (() -> Unit)? = null,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text("Logout clears TokenManager + Encrypted Room DB")
        Spacer(Modifier.height(24.dp))
        onOpenDebugData?.let { openDebugData ->
            Button(
                onClick = openDebugData,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("View DB + SharedPrefs (Debug)")
            }
            Spacer(Modifier.height(12.dp))
        }
        Button(
            onClick = { viewModel.onIntent(ProfileIntent.LogoutClicked) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
