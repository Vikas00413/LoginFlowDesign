package com.example.loginflow.presentation

import androidx.lifecycle.ViewModel
import com.example.loginflow.core.auth.AuthManager
import com.example.loginflow.navigation.SplashDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authManager: AuthManager
) : ViewModel() {

    suspend fun resolveStartDestination(): SplashDestination {
        authManager.initialize()
        return when {
            authManager.isLoggedIn() && authManager.isBiometricEnabled() -> SplashDestination.BIOMETRIC
            authManager.isLoggedIn() -> SplashDestination.PROFILE
            else -> SplashDestination.LOGIN
        }
    }
}
