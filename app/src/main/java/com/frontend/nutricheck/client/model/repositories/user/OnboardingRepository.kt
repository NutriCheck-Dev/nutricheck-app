package com.frontend.nutricheck.client.model.repositories.user

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OnboardingRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val Context.dataStore:
            DataStore<Preferences> by preferencesDataStore(name = "nutricheck_settings")

    private object PreferencesKeys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.ONBOARDING_COMPLETED] == true
        }

    suspend fun setOnboardingCompleted() {
        context.dataStore.edit { settings ->
            settings[PreferencesKeys.ONBOARDING_COMPLETED] = true
        }
    }
}