package com.example.loginflow

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.loginflow.debug.DebugDataRoute
import com.example.loginflow.feature.auth.presentation.login.LoginIntent
import com.example.loginflow.feature.auth.presentation.login.LoginViewModel
import com.example.loginflow.feature.auth.ui.BiometricRoute
import com.example.loginflow.feature.auth.ui.LoginRoute
import com.example.loginflow.feature.profile.ui.ProfileRoute
import com.example.loginflow.feature.profile.ui.SettingsRoute
import com.example.loginflow.navigation.AppAuthNavigator
import com.example.loginflow.navigation.Routes
import com.example.loginflow.navigation.SplashRoute
import com.example.loginflow.social.SocialSignInHelper
import com.example.loginflow.ui.theme.LoginFlowTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject lateinit var appAuthNavigator: AppAuthNavigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LoginFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    DisposableEffect(navController) {
                        appAuthNavigator.attach(navController)
                        onDispose { }
                    }

                    NavHost(
                        navController = navController,
                        startDestination = Routes.SPLASH
                    ) {
                        composable(Routes.SPLASH) {
                            SplashRoute(
                                onNavigateLogin = {
                                    navController.navigate(Routes.LOGIN) {
                                        popUpTo(Routes.SPLASH) { inclusive = true }
                                    }
                                },
                                onNavigateBiometric = {
                                    navController.navigate(Routes.BIOMETRIC) {
                                        popUpTo(Routes.SPLASH) { inclusive = true }
                                    }
                                },
                                onNavigateProfile = {
                                    navController.navigate(Routes.PROFILE) {
                                        popUpTo(Routes.SPLASH) { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable(Routes.LOGIN) {
                            val loginViewModel: LoginViewModel = hiltViewModel()
                            LoginRoute(
                                onGoogleSignIn = {
                                    SocialSignInHelper(
                                        activity = this@MainActivity,
                                        onProviderAuthenticated = { provider ->
                                            loginViewModel.onIntent(LoginIntent.SocialLogin(provider))
                                        },
                                        onError = { }
                                    ).signInWithGoogle()
                                },
                                onFacebookSignIn = {
                                    SocialSignInHelper(
                                        activity = this@MainActivity,
                                        onProviderAuthenticated = { provider ->
                                            loginViewModel.onIntent(LoginIntent.SocialLogin(provider))
                                        },
                                        onError = { }
                                    ).signInWithFacebook()
                                }
                            )
                        }
                        composable(Routes.BIOMETRIC) {
                            BiometricRoute(activity = this@MainActivity)
                        }
                        composable(Routes.PROFILE) {
                            ProfileRoute(
                                onOpenSettings = { navController.navigate(Routes.SETTINGS) }
                            )
                        }
                        composable(Routes.SETTINGS) {
                            SettingsRoute(
                                onOpenDebugData = if (BuildConfig.DEBUG) {
                                    { navController.navigate(Routes.DEBUG_DATA) }
                                } else {
                                    null
                                }
                            )
                        }
                        composable(Routes.DEBUG_DATA) {
                            DebugDataRoute()
                        }
                    }
                }
            }
        }
    }
}
