package com.example.loginflow.core.auth.data

import com.example.loginflow.core.auth.api.AuthApi
import com.example.loginflow.core.auth.api.dto.LoginRequestDto
import com.example.loginflow.core.auth.api.dto.RefreshRequestDto
import com.example.loginflow.core.auth.model.AuthProvider
import com.example.loginflow.core.auth.model.User
import com.example.loginflow.core.auth.security.TokenManager
import com.example.loginflow.core.auth.session.SessionEventBus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authApi: AuthApi,
    private val tokenManager: TokenManager,
    private val localDataSource: AuthLocalDataSource,
    private val sessionEventBus: SessionEventBus
) {
    private val refreshMutex = Mutex()
    private val _authState = MutableStateFlow<com.example.loginflow.core.auth.model.AuthState>(
        com.example.loginflow.core.auth.model.AuthState.Unknown
    )
    val authState: StateFlow<com.example.loginflow.core.auth.model.AuthState> = _authState.asStateFlow()

    suspend fun initialize() {
        if (localDataSource.isLoggedIn() && tokenManager.hasRefreshToken()) {
            val user = localDataSource.getCachedUser()
            if (user != null) {
                _authState.value = com.example.loginflow.core.auth.model.AuthState.LoggedIn(user)
                return
            }
        }
        _authState.value = com.example.loginflow.core.auth.model.AuthState.LoggedOut
    }

    suspend fun login(username: String, password: String): Result<User> = runCatching {
        val response = authApi.login(LoginRequestDto(username, password))
        persistLogin(response, AuthProvider.EMAIL.name.lowercase())
        localDataSource.getCachedUser()!!
    }

    /**
     * DummyJSON has no social endpoint. After Google/Facebook SDK auth we obtain tokens
     * using the demo API user so refresh/me flows still work in this sample app.
     */
    suspend fun loginWithSocialProvider(provider: AuthProvider): Result<User> = runCatching {
        val response = authApi.login(LoginRequestDto("emilys", "emilyspass"))
        persistLogin(response, provider.name.lowercase())
        localDataSource.getCachedUser()!!
    }

    private suspend fun persistLogin(
        response: com.example.loginflow.core.auth.api.dto.LoginResponseDto,
        provider: String
    ) {
        tokenManager.saveTokens(response.accessToken, response.refreshToken, 30)
        localDataSource.saveLogin(response, provider)
        val user = localDataSource.getCachedUser()!!
        _authState.value = com.example.loginflow.core.auth.model.AuthState.LoggedIn(user)
    }

    suspend fun getValidAccessToken(): String? {
        if (tokenManager.isAccessTokenValid()) {
            return tokenManager.getAccessToken()
        }
        return refreshAccessToken()
    }

    suspend fun refreshAccessToken(): String? = refreshMutex.withLock {
        if (tokenManager.isAccessTokenValid()) {
            return tokenManager.getAccessToken()
        }
        val refreshToken = tokenManager.getRefreshToken() ?: return null
        return try {
            val response = authApi.refresh(RefreshRequestDto(refreshToken))
            tokenManager.saveAccessToken(response.accessToken, 30)
            tokenManager.saveRefreshToken(response.refreshToken)
            response.accessToken
        } catch (e: Exception) {
            logout(localOnly = true)
            sessionEventBus.notifySessionExpired()
            null
        }
    }

    suspend fun fetchProfile(): Result<User> = runCatching {
        val profile = authApi.getMe()
        localDataSource.saveUser(profile, localDataSource.getCachedUser()?.provider ?: "email")
        val user = localDataSource.getCachedUser()!!
        _authState.value = com.example.loginflow.core.auth.model.AuthState.LoggedIn(user)
        user
    }

    suspend fun logout(localOnly: Boolean = false) {
        tokenManager.clearAll()
        localDataSource.clearAll()
        _authState.value = com.example.loginflow.core.auth.model.AuthState.LoggedOut
    }

    suspend fun enableBiometric(enabled: Boolean) {
        tokenManager.setBiometricEnabled(enabled)
        localDataSource.setBiometricEnabled(enabled)
    }

    suspend fun isBiometricEnabled(): Boolean = localDataSource.isBiometricEnabled()

    suspend fun isLoggedIn(): Boolean =
        localDataSource.isLoggedIn() && tokenManager.hasRefreshToken()
}
