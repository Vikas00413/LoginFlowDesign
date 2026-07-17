package com.example.loginflow.core.network.authenticator

import com.example.loginflow.core.auth.api.AuthApi
import com.example.loginflow.core.auth.api.dto.RefreshRequestDto
import com.example.loginflow.core.auth.security.TokenManager
import com.example.loginflow.core.auth.session.SessionEventBus
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiProvider: Provider<AuthApi>,
    private val sessionEventBus: SessionEventBus
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.encodedPath.contains("/auth/refresh")) {
            tokenManager.clearAll()
            sessionEventBus.notifySessionExpired()
            return null
        }
        if (responseCount(response) >= 2) return null

        synchronized(this) {
            val currentToken = tokenManager.getAccessToken()
            val requestToken = response.request.header("Authorization")
                ?.removePrefix("Bearer ")

            if (currentToken != null && currentToken != requestToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentToken")
                    .build()
            }

            val refreshToken = tokenManager.getRefreshToken() ?: run {
                sessionEventBus.notifySessionExpired()
                return null
            }

            return try {
                val refreshResponse = runBlocking {
                    authApiProvider.get().refresh(RefreshRequestDto(refreshToken))
                }
                tokenManager.saveAccessToken(refreshResponse.accessToken, 30)
                tokenManager.saveRefreshToken(refreshResponse.refreshToken)
                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResponse.accessToken}")
                    .build()
            } catch (e: Exception) {
                tokenManager.clearAll()
                sessionEventBus.notifySessionExpired()
                null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
