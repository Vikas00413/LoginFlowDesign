package com.example.loginflow.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.loginflow.presentation.SplashViewModel

@Composable
fun SplashRoute(
    onNavigateLogin: () -> Unit,
    onNavigateBiometric: () -> Unit,
    onNavigateProfile: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        when (val dest = viewModel.resolveStartDestination()) {
            SplashDestination.LOGIN -> onNavigateLogin()
            SplashDestination.BIOMETRIC -> onNavigateBiometric()
            SplashDestination.PROFILE -> onNavigateProfile()
        }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

enum class SplashDestination {
    LOGIN, BIOMETRIC, PROFILE
}
