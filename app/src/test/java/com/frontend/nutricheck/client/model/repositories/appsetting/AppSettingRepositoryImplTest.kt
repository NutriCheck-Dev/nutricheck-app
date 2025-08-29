package com.frontend.nutricheck.client.model.repositories.appsetting

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepositoryImpl
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Unit test class for [AppSettingRepositoryImpl]
 */
@ExperimentalCoroutinesApi
class AppSettingRepositoryImplTest {

    private val mockDataStore = mockk<DataStore<Preferences>>()
    private val mockPreferences = mockk<Preferences>()
    private val mockMutablePreferences = mockk<MutablePreferences>(relaxed = true)

    private lateinit var repository: AppSettingRepositoryImpl

    // Preference keys used in the implementation
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")
    private val themeKey = booleanPreferencesKey("theme")
    private val languageKey = stringPreferencesKey("language")

    /**
     * Sets up the test environment before each test.
     * Initializes the repository with mocked DataStore dependency.
     */
    @Before
    fun setUp() {
        // Mock the data flow before the repository is initialized
        every { mockDataStore.data } returns flowOf(mockPreferences)

        // Initialize the repository. Now it has a valid mock to work with.
        repository = AppSettingRepositoryImpl(mockDataStore)
    }

    /**
     * Tests isOnboardingCompleted flow when onboarding is completed.
     * Verifies that the flow returns true when the preference is set to true.
     */
    @Test
    fun `isOnboardingCompleted returns true when preference is true`() = runTest {
        every { mockPreferences[onboardingCompletedKey] } returns true

        val result = repository.isOnboardingCompleted.first()

        assertTrue(result)
    }

    /**
     * Tests isOnboardingCompleted flow when onboarding is not completed.
     * Verifies that the flow returns false when the preference is set to false.
     */
    @Test
    fun `isOnboardingCompleted returns false when preference is false`() = runTest {
        every { mockPreferences[onboardingCompletedKey] } returns false

        val result = repository.isOnboardingCompleted.first()

        assertEquals(false, result)
    }

    /**
     * Tests isOnboardingCompleted flow when no preference is set.
     * Verifies that the flow returns false as the default value when preference is null.
     */
    @Test
    fun `isOnboardingCompleted returns false when preference is null`() = runTest {
        every { mockPreferences[onboardingCompletedKey] } returns null

        val result = repository.isOnboardingCompleted.first()

        assertEquals(false, result)
    }

    /**
     * Tests theme flow when dark mode is enabled.
     * Verifies that the flow returns true when the preference is set to true.
     */
    @Test
    fun `theme returns true when dark mode is enabled`() = runTest {
        every { mockPreferences[themeKey] } returns true

        val result = repository.theme.first()

        assertTrue(result)
    }

    /**
     * Tests theme flow when light mode is enabled.
     * Verifies that the flow returns false when the preference is set to false.
     */
    @Test
    fun `theme returns false when light mode is enabled`() = runTest {
        every { mockPreferences[themeKey] } returns false

        val result = repository.theme.first()

        assertEquals(false, result)
    }

    /**
     * Tests theme flow when no preference is set.
     * Verifies that the flow returns true as the default value (dark mode) when preference is null.
     */
    @Test
    fun `theme returns true by default when preference is null`() = runTest {
        every { mockPreferences[themeKey] } returns null

        val result = repository.theme.first()

        assertTrue(result)
    }

    /**
     * Tests language flow when German language is set.
     * Verifies that the flow returns German language when the preference is set to "de".
     */
    @Test
    fun `language returns German when preference is de`() = runTest {
        every { mockPreferences[languageKey] } returns "de"

        val result = repository.language.first()

        assertEquals(Language.GERMAN, result)
    }

    /**
     * Tests language flow when English language is set.
     * Verifies that the flow returns English language when the preference is set to "en".
     */
    @Test
    fun `language returns English when preference is en`() = runTest {
        every { mockPreferences[languageKey] } returns "en"

        val result = repository.language.first()

        assertEquals(Language.ENGLISH, result)
    }

    /**
     * Tests language flow when no preference is set.
     * Verifies that the flow returns German as the default language when preference is null.
     */
    @Test
    fun `language returns German by default when preference is null`() = runTest {
        every { mockPreferences[languageKey] } returns null

        val result = repository.language.first()

        assertEquals(Language.GERMAN, result)
    }

    /**
     * Tests language flow when an invalid language code is set.
     * Verifies that the flow returns German as the fallback when an unknown language code is stored.
     */
    @Test
    fun `language returns German when invalid language code is stored`() = runTest {
        every { mockPreferences[languageKey] } returns "invalid"

        val result = repository.language.first()

        assertEquals(Language.GERMAN, result)
    }


}
