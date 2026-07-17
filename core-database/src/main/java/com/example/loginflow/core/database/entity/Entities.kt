package com.example.loginflow.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String,
    val provider: String = "email"
)

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val id: Int = 1,
    val userId: Int,
    val isLoggedIn: Boolean,
    val biometricEnabled: Boolean = false,
    val accessTokenExpiresAt: Long = 0L
)
