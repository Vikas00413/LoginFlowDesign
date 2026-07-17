package com.example.loginflow.feature.profile.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.loginflow.feature.profile.presentation.profile.ProfileIntent
import com.example.loginflow.feature.profile.presentation.profile.ProfileState
import com.example.loginflow.feature.profile.presentation.profile.ProfileViewModel

@Composable
fun ProfileRoute(
    onOpenSettings: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ProfileScreen(
        state = state,
        onIntent = viewModel::onIntent,
        onOpenSettings = onOpenSettings
    )
}

@Composable
fun ProfileScreen(
    state: ProfileState,
    onIntent: (ProfileIntent) -> Unit,
    onOpenSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Profile", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        when {
            state.isLoading -> CircularProgressIndicator()
            state.user != null -> {
                AsyncImage(
                    model = state.user.imageUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.size(96.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(Modifier.height(16.dp))
                Text(state.user.displayName, style = MaterialTheme.typography.titleLarge)
                Text(state.user.email)
                Text("Provider: ${state.user.provider}")
                Text("@${state.user.username}")
            }
            else -> Text(state.error ?: "No user")
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { onIntent(ProfileIntent.LoadProfile) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Refresh from /auth/me")
        }
        OutlinedButton(onClick = onOpenSettings, modifier = Modifier.fillMaxWidth()) {
            Text("Settings / Logout")
        }
    }
}
