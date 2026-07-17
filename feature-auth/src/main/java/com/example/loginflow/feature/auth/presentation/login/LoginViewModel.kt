package com.example.loginflow.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginflow.core.auth.AuthManager
import com.example.loginflow.feature.auth.navigation.AuthNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val authNavigator: AuthNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    private val _effects = Channel<LoginEffect>(Channel.BUFFERED)
    val effects = _effects.receiveAsFlow()

    fun onIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.UsernameChanged -> reduceUsernameChanged(intent.value)
            is LoginIntent.PasswordChanged -> reducePasswordChanged(intent.value)
            LoginIntent.LoginClicked -> login()
            is LoginIntent.SocialLogin -> socialLogin(intent.provider)
            is LoginIntent.BiometricChoice -> setBiometricAndNavigate(intent.enable)
            LoginIntent.SkipBiometric -> navigateToProfile()
        }
    }

    private fun reduceUsernameChanged(value: String) {
        _state.update { it.copy(username = value, error = null) }
    }

    private fun reducePasswordChanged(value: String) {
        _state.update { it.copy(password = value, error = null) }
    }

    private fun login() {
        val credentials = _state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authManager.login(credentials.username, credentials.password)
                .onSuccess { onLoginSuccess() }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message ?: "Login failed")
                    }
                }
        }
    }

    private fun socialLogin(provider: com.example.loginflow.core.auth.model.AuthProvider) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authManager.loginWithSocial(provider)
                .onSuccess { onLoginSuccess() }
                .onFailure { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message ?: "Social login failed")
                    }
                }
        }
    }

    private suspend fun onLoginSuccess() {
        _state.update { it.copy(isLoading = false) }
        dispatchEffect(LoginEffect.ShowBiometricDialog)
    }

    private fun setBiometricAndNavigate(enable: Boolean) {
        viewModelScope.launch {
            authManager.enableBiometric(enable)
            navigateToProfile()
        }
    }

    private fun navigateToProfile() {
        viewModelScope.launch {
            dispatchEffect(LoginEffect.NavigateToProfile)
        }
    }

    private suspend fun dispatchEffect(effect: LoginEffect) {
        when (effect) {
            LoginEffect.NavigateToProfile -> authNavigator.onLoginSuccess()
            else -> _effects.send(effect)
        }
    }
}
