package com.frontend.nutricheck.client.model.repositories.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepository @Inject constructor(
    private val dataStore : DataStore<Preferences>
) {
    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val THEME_DARK = booleanPreferencesKey("theme") // true for dark mode, false for light mode
        val LANGUAGE = stringPreferencesKey("language")
    }

    val isOnboardingCompleted: Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] == true
        }.flowOn(Dispatchers.IO)
    val theme : Flow<Boolean> = dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.THEME_DARK] != false  // Default to dark mode if not set
        }.flowOn(Dispatchers.IO)
    val language: Flow<Language> = dataStore.data
        .map { preferences ->
            Language.entries.find { it.code == (preferences[PreferencesKeys.LANGUAGE] ?: "de") }
                ?: Language.GERMAN // Default to German if not found
        }.flowOn(Dispatchers.IO)

    suspend fun setLanguage(language: Language) = withContext(Dispatchers.IO) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.LANGUAGE] = language.code
        }
    }

    suspend fun setTheme(isDarkMode: Boolean) = withContext(Dispatchers.IO) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.THEME_DARK] = isDarkMode
        }
    }
    suspend fun setOnboardingCompleted() = withContext(Dispatchers.IO) {
        dataStore.edit { settings ->
            settings[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }
}