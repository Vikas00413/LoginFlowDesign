package com.example.loginflow.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.loginflow.core.database.dao.SessionDao
import com.example.loginflow.core.database.dao.UserDao
import com.example.loginflow.core.database.entity.SessionEntity
import com.example.loginflow.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class, SessionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
}
