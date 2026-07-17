package com.example.loginflow.social

import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.loginflow.core.auth.model.AuthProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class SocialSignInHelper(
    private val activity: ComponentActivity,
    private val onProviderAuthenticated: (AuthProvider) -> Unit,
    private val onError: (String) -> Unit
) {
    private val facebookCallbackManager = CallbackManager.Factory.create()

    private val googleLauncher = activity.registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            task.getResult(ApiException::class.java)
            onProviderAuthenticated(AuthProvider.GOOGLE)
        } catch (e: Exception) {
            onError(e.message ?: "Google sign-in failed")
        }
    }

    init {
        LoginManager.getInstance().registerCallback(
            facebookCallbackManager,
            object : FacebookCallback<com.facebook.login.LoginResult> {
                override fun onSuccess(result: com.facebook.login.LoginResult) {
                    onProviderAuthenticated(AuthProvider.FACEBOOK)
                }

                override fun onCancel() = onError("Facebook login cancelled")
                override fun onError(error: FacebookException) =
                    onError(error.message ?: "Facebook login failed")
            }
        )
    }

    fun signInWithGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        val client = GoogleSignIn.getClient(activity, gso)
        googleLauncher.launch(client.signInIntent)
    }

    fun signInWithFacebook() {
        LoginManager.getInstance()
            .logInWithReadPermissions(activity, listOf("email", "public_profile"))
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: android.content.Intent?) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
    }
}
