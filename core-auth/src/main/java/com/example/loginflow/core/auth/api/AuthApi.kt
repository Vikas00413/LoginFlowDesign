package com.example.loginflow.core.auth.api

import com.example.loginflow.core.auth.api.dto.LoginRequestDto
import com.example.loginflow.core.auth.api.dto.LoginResponseDto
import com.example.loginflow.core.auth.api.dto.RefreshRequestDto
import com.example.loginflow.core.auth.api.dto.RefreshResponseDto
import com.example.loginflow.core.auth.api.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body body: LoginRequestDto): LoginResponseDto

    @GET("auth/me")
    suspend fun getMe(): UserDto

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshRequestDto): RefreshResponseDto
}
