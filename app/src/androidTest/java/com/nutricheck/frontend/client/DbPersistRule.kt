package com.nutricheck.frontend.client

import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import java.io.File

class DbPersistRule(
    private val dbName: String = "test_database"
) : TestWatcher() {

    private val target = InstrumentationRegistry.getInstrumentation().targetContext
    private val testContext = InstrumentationRegistry.getInstrumentation().context

    override fun starting(description: Description) {
        val backup = File(target.noBackupFilesDir, "$dbName-backup.db")

        val destination = target.getDatabasePath(dbName)
        destination.parentFile?.mkdirs()

        if (backup.exists() && !destination.exists()) {
            backup.copyTo(destination, overwrite = true)
        }
    }

    override fun finished(description: Description) {
        val source = target.getDatabasePath(dbName)
        if (!source.exists()) return

        val backup = File(target.noBackupFilesDir, "$dbName-backup.db")
        backup.parentFile?.mkdirs()
        if (backup.exists()) backup.delete()

        val escaped = backup.absolutePath.replace("'", "''")
        val database = SQLiteDatabase.openDatabase(source.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)

        try {
            database.rawQuery("PRAGMA wal_checkpoint(FULL);", null).use { }
            database.execSQL("VACUUM INTO '$escaped';")
        } finally {
            database.close()
        }
    }
}