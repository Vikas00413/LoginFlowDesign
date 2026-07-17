package com.example.loginflow.feature.profile.presentation.profile

import com.example.loginflow.core.auth.model.User

data class ProfileState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val error: String? = null
)
