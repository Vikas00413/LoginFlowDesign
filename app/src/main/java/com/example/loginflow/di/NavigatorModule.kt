package com.example.loginflow.di

import com.example.loginflow.feature.auth.navigation.AuthNavigator
import com.example.loginflow.navigation.AppAuthNavigator
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NavigatorModule {
    @Binds
    @Singleton
    abstract fun bindAuthNavigator(impl: AppAuthNavigator): AuthNavigator
}
