package com.example.loginflow.feature.profile.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.loginflow.core.auth.AuthManager
import com.example.loginflow.core.auth.model.AuthState
import com.example.loginflow.feature.auth.navigation.AuthNavigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authManager: AuthManager,
    private val authNavigator: AuthNavigator
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        onIntent(ProfileIntent.LoadProfile)
    }

    fun onIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.LoadProfile -> loadProfile()
            ProfileIntent.LogoutClicked -> logout()
        }
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            authManager.fetchProfile()
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user) }
                }
                .onFailure { error ->
                    val cached = (authManager.authState.value as? AuthState.LoggedIn)?.user
                    _state.update {
                        it.copy(
                            isLoading = false,
                            user = cached,
                            error = error.message ?: "Failed to load profile"
                        )
                    }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            authManager.logout(localOnly = true)
            authNavigator.onLogout()
        }
    }
}
