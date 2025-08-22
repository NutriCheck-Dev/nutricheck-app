package com.frontend.nutricheck.client.model.repositories.appSetting

import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing application settings.
 */
interface AppSettingRepository {
    /**
     * Flow that emits whether the onboarding process is completed.
     */
    val isOnboardingCompleted: Flow<Boolean>

    /**
     * Flow that emits the current theme setting (dark mode or light mode).
     */
    val theme: Flow<Boolean>

    /**
     * Flow that emits the current language setting.
     */
    val language: Flow<Language>

    /**
     * Sets the language for the application.
     */
    suspend fun setLanguage(language: Language)

    /**
     * Sets the theme for the application.
     */
    suspend fun setTheme(isDarkMode: Boolean)

    /**
     * Sets the onboarding process as completed.
     */
    suspend fun setOnboardingCompleted()
}