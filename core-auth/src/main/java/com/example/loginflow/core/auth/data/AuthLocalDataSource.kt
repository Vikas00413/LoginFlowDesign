package com.example.loginflow.core.auth.data

import com.example.loginflow.core.auth.api.dto.LoginResponseDto
import com.example.loginflow.core.auth.api.dto.UserDto
import com.example.loginflow.core.auth.model.User
import com.example.loginflow.core.database.dao.SessionDao
import com.example.loginflow.core.database.dao.UserDao
import com.example.loginflow.core.database.entity.SessionEntity
import com.example.loginflow.core.database.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthLocalDataSource @Inject constructor(
    private val userDao: UserDao,
    private val sessionDao: SessionDao
) {
    suspend fun saveLogin(response: LoginResponseDto, provider: String) {
        userDao.insert(response.toEntity(provider))
        sessionDao.insert(
            SessionEntity(
                userId = response.id,
                isLoggedIn = true,
                biometricEnabled = false
            )
        )
    }

    suspend fun saveUser(user: UserDto, provider: String) {
        userDao.insert(user.toEntity(provider))
    }

    suspend fun getCachedUser(): User? = userDao.getUser()?.toDomain()

    suspend fun isLoggedIn(): Boolean = sessionDao.getSession()?.isLoggedIn == true

    suspend fun setBiometricEnabled(enabled: Boolean) {
        val session = sessionDao.getSession() ?: return
        sessionDao.insert(session.copy(biometricEnabled = enabled))
    }

    suspend fun isBiometricEnabled(): Boolean =
        sessionDao.getSession()?.biometricEnabled == true

    suspend fun clearAll() {
        userDao.clear()
        sessionDao.clear()
    }
}

private fun LoginResponseDto.toEntity(provider: String) = UserEntity(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    imageUrl = image,
    provider = provider
)

private fun UserDto.toEntity(provider: String) = UserEntity(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    imageUrl = image,
    provider = provider
)

private fun UserEntity.toDomain() = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    imageUrl = imageUrl,
    provider = provider
)
