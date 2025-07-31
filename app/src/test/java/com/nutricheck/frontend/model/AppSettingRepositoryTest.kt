package com.nutricheck.frontend.model

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.frontend.nutricheck.client.model.data_sources.data.Language
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.Assert.assertEquals
import java.io.File

@ExperimentalCoroutinesApi
class AppSettingsRepositoryTest {
    @get:Rule
    val tempFolder = TemporaryFolder()

    private lateinit var testDataStore: androidx.datastore.core.DataStore<androidx.datastore.preferences.core.Preferences>
    private lateinit var repository: AppSettingsRepository
    private lateinit var tempFile: File

    @Before
    fun setUp() {
        // Create a unique temporary file for each test
        tempFile = tempFolder.newFile("test_settings_${System.nanoTime()}.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            produceFile = { tempFile }
        )
        repository = AppSettingsRepository(testDataStore)
    }

    @After
    fun tearDown() {
        // Explicitly delete the temporary file to ensure cleanup
        if (tempFile.exists()) {
            println("Deleting temporary file: ${tempFile.absolutePath}")
            tempFile.delete()
        }
    }

    @Test
    fun `default onboarding is not completed`() = runTest(UnconfinedTestDispatcher()) {
        val result = repository.isOnboardingCompleted.first()
        assertEquals(false, result)
    }


    @Test
    fun `default theme is dark`() = runTest(UnconfinedTestDispatcher()) {
        val result = repository.theme.first()
        assertEquals(true, result)
    }


    @Test
    fun `default language is German`() = runTest(UnconfinedTestDispatcher()) {
        val result = repository.language.first()
        assertEquals(Language.GERMAN, result)
    }

   
}