package com.frontend.nutricheck.client.model.repositories.appSetting

import com.frontend.nutricheck.client.model.data_sources.data.flags.Language
import kotlinx.coroutines.flow.Flow

interface AppSettingRepository {
    val isOnboardingCompleted: Flow<Boolean>
    val theme: Flow<Boolean>
    val language: Flow<Language>

    suspend fun setLanguage(language: Language)
    suspend fun setTheme(isDarkMode: Boolean)
    suspend fun setOnboardingCompleted()
}