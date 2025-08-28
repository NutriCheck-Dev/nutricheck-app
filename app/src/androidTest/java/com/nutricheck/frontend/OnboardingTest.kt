package com.nutricheck.frontend

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.nutricheck.frontend.util.DbPersistRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Date
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingTest {

    @get:Rule(order = -1)
    val dbPersist = DbPersistRule()

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var db: LocalDatabase

    @Inject
    lateinit var dataStore : DataStore<Preferences>

    @Before
    fun setUp() {
        hilt.inject()
        clearOnboardingStatus()

    }

    @After
    fun tearDown() = runBlocking{
        db.close()
    }

    @Test
    fun onboarding_completeFlowWithValidData_navigatesToDashboard() {
        waitForOnboardingWelcomeScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_start))
            .assertIsDisplayed()
            .performClick()

        // Try empty name
        waitForNameScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_error_name_required))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_question_name)).assertIsDisplayed()

        // Fill in name
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_name))
            .performTextInput("Max Mustermann")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Fill in birthdate
        waitForBirthdateScreen()
        selectBirthdate(1990, 1, 1)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Select gender
        waitForGenderScreen()
        selectGender(Gender.MALE)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Try invalid height
        waitForHeightScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_height)).performTextInput("0")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_error_height_too_short))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_question_height)).assertIsDisplayed()

        // Fill in height
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_height)).performTextInput("180")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Try invalid weight
        waitForWeightScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_weight)).performTextInput("-5")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_error_weight_too_low))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_question_weight)).assertIsDisplayed()

        // Fill in weight
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_weight)).performTextInput("75")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Select activity level
        waitForActivityLevelScreen()
        selectActivityLevel(ActivityLevel.OCCASIONALLY)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Select weight goal
        waitForWeightGoalScreen()
        selectWeightGoal(WeightGoal.MAINTAIN_WEIGHT)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Fill in target weight
        waitForTargetWeightScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_target_weight)).performTextInput("75")
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_finish)).performClick()

        // Verify navigation to dashboard
        waitForDashboard()

        // Verify user data is saved
        verifyUserDataSaved()
    }



    // Helper methods
    private fun waitForOnboardingWelcomeScreen() {
        compose.waitUntil(5000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_title))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForNameScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_name))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForBirthdateScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_birthdate))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForGenderScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_gender))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForHeightScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_height))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForWeightScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_weight))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForActivityLevelScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_activity_level))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForWeightGoalScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_goal))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForTargetWeightScreen() {
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.onboarding_question_target_weight))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitForDashboard() {
        compose.waitUntil(5000) {
            // Macronutrition label is only on the dashboard screen
            compose.onAllNodesWithText(compose.activity.getString(R.string.label_macronutrition))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun selectBirthdate(year: Int, month: Int, day: Int) {
        // This depends on your date picker implementation
        // You might need to interact with a date picker dialog or input fields
        // For now, assuming there are separate input fields or a date picker
        // Adjust according to your actual UI implementation
    }

    private fun selectGender(gender: Gender) {
        val genderText = when (gender) {
            Gender.MALE -> compose.activity.getString(R.string.userData_label_gender_male)
            Gender.FEMALE -> compose.activity.getString(R.string.userData_label_gender_female)
            Gender.DIVERS -> compose.activity.getString(R.string.userData_label_gender_diverse)
        }
        compose.onNodeWithText(genderText).performClick()
    }

    private fun selectActivityLevel(activityLevel: ActivityLevel) {
        val activityText = when (activityLevel) {
            ActivityLevel.NEVER -> compose.activity.getString(R.string.userData_label_activity_level_never)
            ActivityLevel.OCCASIONALLY -> compose.activity.getString(R.string.userData_label_activity_level_occasionally)
            ActivityLevel.REGULARLY -> compose.activity.getString(R.string.userData_label_activity_level_regularly)
            ActivityLevel.FREQUENTLY -> compose.activity.getString(R.string.userData_label_activity_level_frequently)
        }
        compose.onNodeWithText(activityText).performClick()
    }

    private fun selectWeightGoal(weightGoal: WeightGoal) {
        val goalText = when (weightGoal) {
            WeightGoal.LOSE_WEIGHT -> compose.activity.getString(R.string.userData_label_goal_lose_weight)
            WeightGoal.MAINTAIN_WEIGHT-> compose.activity.getString(R.string.userData_label_goal_maintain_weight)
            WeightGoal.GAIN_WEIGHT -> compose.activity.getString(R.string.userData_label_goal_gain_weight)
        }
        compose.onNodeWithText(goalText).performClick()
    }

    private fun clearOnboardingStatus() = runBlocking{
        val key = booleanPreferencesKey("onboarding_completed")
        dataStore.edit { prefs ->
            prefs.remove(key)
        }
    }

    private fun verifyUserDataSaved() = runBlocking {
        val userData = db.userDataDao().getUserData()

        assert(userData != null)
        assert(userData?.username == "Max Mustermann")
        assert(userData?.height == 180.0)
        assert(userData?.weight == 75.0)
        assert(userData?.targetWeight == 75.0)
        assert(userData?.activityLevel == ActivityLevel.OCCASIONALLY)
        assert(userData?.weightGoal == WeightGoal.MAINTAIN_WEIGHT)
    }
}