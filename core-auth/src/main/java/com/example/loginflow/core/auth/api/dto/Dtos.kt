package com.example.loginflow.core.auth.api.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    val username: String,
    val password: String,
    val expiresInMins: Int = 30
)

data class LoginResponseDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String?,
    val image: String,
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class RefreshRequestDto(
    val refreshToken: String,
    val expiresInMins: Int = 30
)

data class RefreshResponseDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)

data class UserDto(
    val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val gender: String?,
    val image: String
)
