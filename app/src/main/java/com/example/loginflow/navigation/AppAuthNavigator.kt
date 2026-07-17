package com.example.loginflow.navigation

import androidx.navigation.NavController
import com.example.loginflow.feature.auth.navigation.AuthNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppAuthNavigator @Inject constructor() : AuthNavigator {
    private var navController: NavController? = null

    fun attach(navController: NavController) {
        this.navController = navController
    }

    override fun onLoginSuccess() {
        navController?.navigate(Routes.PROFILE) {
            popUpTo(Routes.LOGIN) { inclusive = true }
        }
    }

    override fun onLogout() {
        navController?.navigate(Routes.LOGIN) {
            popUpTo(Routes.SPLASH) { inclusive = false }
        }
    }
}

object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val BIOMETRIC = "biometric"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"
    const val DEBUG_DATA = "debug_data"
}
