package com.example.loginflow.core.auth

import com.example.loginflow.core.auth.data.AuthRepository
import com.example.loginflow.core.auth.model.AuthState
import com.example.loginflow.core.auth.model.User
import com.example.loginflow.core.auth.session.SessionEventBus
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

interface AuthManager {
    val authState: StateFlow<AuthState>
    val sessionExpired: SharedFlow<Unit>
    suspend fun initialize()
    suspend fun login(username: String, password: String): Result<User>
    suspend fun loginWithSocial(provider: com.example.loginflow.core.auth.model.AuthProvider): Result<User>
    suspend fun getValidAccessToken(): String?
    suspend fun refreshAccessToken(): String?
    suspend fun fetchProfile(): Result<User>
    suspend fun logout(localOnly: Boolean = false)
    suspend fun enableBiometric(enabled: Boolean)
    suspend fun isBiometricEnabled(): Boolean
    suspend fun isLoggedIn(): Boolean
}

@Singleton
class AuthManagerImpl @Inject constructor(
    private val repository: AuthRepository,
    private val sessionEventBus: SessionEventBus
) : AuthManager {
    override val authState: StateFlow<AuthState> = repository.authState
    override val sessionExpired: SharedFlow<Unit> = sessionEventBus.sessionExpired

    override suspend fun initialize() = repository.initialize()
    override suspend fun login(username: String, password: String) = repository.login(username, password)
    override suspend fun loginWithSocial(provider: com.example.loginflow.core.auth.model.AuthProvider) =
        repository.loginWithSocialProvider(provider)
    override suspend fun getValidAccessToken(): String? = repository.getValidAccessToken()
    override suspend fun refreshAccessToken(): String? = repository.refreshAccessToken()
    override suspend fun fetchProfile(): Result<User> = repository.fetchProfile()
    override suspend fun logout(localOnly: Boolean) = repository.logout(localOnly)
    override suspend fun enableBiometric(enabled: Boolean) = repository.enableBiometric(enabled)
    override suspend fun isBiometricEnabled(): Boolean = repository.isBiometricEnabled()
    override suspend fun isLoggedIn(): Boolean = repository.isLoggedIn()
}
