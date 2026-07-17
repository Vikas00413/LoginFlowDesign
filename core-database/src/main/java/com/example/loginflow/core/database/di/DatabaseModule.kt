package com.example.loginflow.core.database

import android.content.Context
import androidx.room.Room
import com.example.loginflow.core.database.security.DatabasePassphraseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        passphraseManager: DatabasePassphraseManager
    ): AppDatabase {
        val factory = SupportOpenHelperFactory(passphraseManager.getPassphrase())
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "login_flow_encrypted.db"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase) = database.userDao()

    @Provides
    fun provideSessionDao(database: AppDatabase) = database.sessionDao()
}
