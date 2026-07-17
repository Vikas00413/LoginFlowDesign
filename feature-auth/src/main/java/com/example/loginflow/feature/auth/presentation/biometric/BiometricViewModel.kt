package com.example.loginflow.feature.auth.presentation.biometric

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginflow.core.auth.AuthManager
import com.example.loginflow.feature.auth.navigation.AuthNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val authNavigator: AuthNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(BiometricState())
    val state: StateFlow<BiometricState> = _state.asStateFlow()

    fun onIntent(intent: BiometricIntent) {
        when (intent) {
            BiometricIntent.BiometricSuccess -> onBiometricSuccess()
            BiometricIntent.BiometricError -> onBiometricError()
        }
    }

    private fun onBiometricSuccess() {
        viewModelScope.launch {
            _state.value = BiometricState(isLoading = true)
            val token = authManager.refreshAccessToken() ?: authManager.getValidAccessToken()
            if (token != null) {
                authNavigator.onLoginSuccess()
            } else {
                _state.value = BiometricState(error = "Session expired")
                authNavigator.onLogout()
            }
        }
    }

    private fun onBiometricError() {
        authNavigator.onLogout()
    }
}
