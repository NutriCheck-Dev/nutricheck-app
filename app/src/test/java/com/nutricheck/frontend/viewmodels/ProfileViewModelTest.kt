package com.nutricheck.frontend.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_sources.data.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.Gender
import com.frontend.nutricheck.client.model.data_sources.data.Language
import com.frontend.nutricheck.client.model.data_sources.data.ThemeSetting
import com.frontend.nutricheck.client.model.data_sources.data.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.UserData
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.Weight
import com.frontend.nutricheck.client.model.repositories.user.AppSettingsRepository
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileEvent
import com.frontend.nutricheck.client.ui.view_model.profile.ProfileViewModel
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.mockito.MockitoAnnotations
import java.util.Date

@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var userDataRepository: UserDataRepository

    private lateinit var appSettingsRepository: AppSettingsRepository

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        viewModel = ProfileViewModel(userDataRepository, appSettingsRepository)
    }

    class MainCoroutineRule : TestWatcher() {
        override fun starting(description: Description?) {
            Dispatchers.setMain(StandardTestDispatcher())
        }

        override fun finished(description: Description?) {
            Dispatchers.resetMain()
        }
    }
}