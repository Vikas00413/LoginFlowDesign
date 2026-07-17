package com.example.loginflow.core.network.interceptor

import com.example.loginflow.core.auth.security.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.isAuthEndpoint()) {
            return chain.proceed(original)
        }
        val token = tokenManager.getAccessToken()
        val request = if (!token.isNullOrBlank()) {
            original.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            original
        }
        return chain.proceed(request)
    }

    private fun okhttp3.Request.isAuthEndpoint(): Boolean {
        val path = url.encodedPath
        return path.contains("/auth/login") || path.contains("/auth/refresh")
    }
}
