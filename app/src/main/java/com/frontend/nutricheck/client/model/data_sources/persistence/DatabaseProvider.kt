package com.frontend.nutricheck.client.model.data_sources.persistence

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var Instance: LocalDatabase? = null

    fun getDatabase(context: Context): LocalDatabase {
        return Instance ?: synchronized(this) {
            Room.databaseBuilder(
                        context.applicationContext,
                        LocalDatabase::class.java,
                        "nutricheck_database"
                    ).fallbackToDestructiveMigration(true)
                .build()
                .also { Instance = it }

        }
    }
}