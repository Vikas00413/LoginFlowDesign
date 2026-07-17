package com.example.loginflow

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class LoginFlowApp : Application() {

    override fun onCreate() {
        super.onCreate()
        System.loadLibrary("sqlcipher")
    }
}
