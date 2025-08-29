package com.nutricheck.frontend.util

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.core.DataStore
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface OnboardingReposEntryPoint {
    fun appSettingsRepository(): AppSettingRepository
    fun userDataRepository(): UserDataRepository
    fun dataStore() : DataStore<Preferences>
}