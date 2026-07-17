package com.example.loginflow.feature.profile.presentation.profile

sealed interface ProfileIntent {
    data object LoadProfile : ProfileIntent
    data object LogoutClicked : ProfileIntent
}
