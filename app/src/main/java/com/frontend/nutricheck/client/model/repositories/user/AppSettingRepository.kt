package com.frontend.nutricheck.client.model.repositories.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.frontend.nutricheck.client.model.data_sources.data.Language
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore:
            DataStore<Preferences> by preferencesDataStore(name = "nutricheck_settings")

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_DARK = booleanPreferencesKey("theme") // true for dark mode, false for light mode
        val LANGUAGE = stringPreferencesKey("language")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] == true
        }
    val theme : Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_DARK] != false  // Default to dark mode if not set
        }
    val language: Flow<Language> = context.dataStore.data
        .map { preferences ->
            Language.entries.find { it.code == (preferences[PreferencesKeys.LANGUAGE] ?: "de") }
                ?: Language.GERMAN // Default to German if not found
        }
    suspend fun setLanguage(language: Language) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.LANGUAGE] = language.code
        }
    }

    suspend fun setTheme(isDarkMode: Boolean) {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.THEME_DARK] = isDarkMode
        }
    }
    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }
}