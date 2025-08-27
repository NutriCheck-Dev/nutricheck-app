package com.nutricheck.frontend.util

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
}