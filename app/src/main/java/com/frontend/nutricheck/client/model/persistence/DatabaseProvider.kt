package com.frontend.nutricheck.client.model.persistence

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var _instance: LocalDatabase? = null

    fun getDatabase(context: Context): LocalDatabase {
        return _instance ?: synchronized(this) {
            val instance = Room.databaseBuilder(
                context.applicationContext,
                LocalDatabase::class.java,
                "nutricheck_database"
            ).build()

            _instance = instance
            instance
        }
    }
}