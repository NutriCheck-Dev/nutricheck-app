package com.nutricheck.frontend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.test.uiautomator.UiDevice
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.DayTime
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.frontend.nutricheck.client.model.data_sources.persistence.entity.MealEntity
import com.nutricheck.frontend.util.BypassOnboardingRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class StartAppTest {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val compose = createComposeRule()

    @Inject lateinit var db: LocalDatabase

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        hilt.inject()
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        setupTestData()
    }

    private fun setupTestData() : Unit = runBlocking {
        val mealEntity = MealEntity(
            id = "apple",
            historyDayDate = Date(),
            dayTime = DayTime.BREAKFAST,
            calories = 95.0,
            carbohydrates = 20.0,
            protein = 1.0,
            fat = 0.0
        )
        db.mealDao().insert(mealEntity)
    }



    @Test
    fun appStartsAndShowsDashboardWithStoredData() {
        // start app
        val launcherPackage: String = device.launcherPackageName
        compose.waitForIdle()
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), 5000L)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(intent)

        // verify that the dashboard is displayed with the stored data
        device.wait(Until.hasObject(By.pkg(context.packageName).depth(0)), 5000L)
        compose.waitUntil(timeoutMillis = 20_000) {
            compose.onAllNodesWithText(InstrumentationRegistry.getInstrumentation().targetContext
                .getString(R.string.homepage_calorie_history)).fetchSemanticsNodes().isNotEmpty()
        }
        verifyDailyNutritionValues()
        compose.onNodeWithText(InstrumentationRegistry.getInstrumentation().targetContext
            .getString(R.string.homepage_weight_progress)).assertIsDisplayed()
    }

    private fun verifyDailyNutritionValues() {
        compose.onNodeWithText("1g").assertIsDisplayed() // protein value
        compose.onNodeWithText("20g").assertIsDisplayed() // carbohydrate value
        compose.onNodeWithText("0g").assertIsDisplayed() // fat value
    }

}