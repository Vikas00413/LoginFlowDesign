package com.example.loginflow.core.auth.di

import com.example.loginflow.core.auth.AuthManager
import com.example.loginflow.core.auth.AuthManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {
    @Binds
    @Singleton
    abstract fun bindAuthManager(impl: AuthManagerImpl): AuthManager
}
