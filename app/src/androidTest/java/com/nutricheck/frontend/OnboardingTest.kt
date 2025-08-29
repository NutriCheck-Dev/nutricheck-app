package com.nutricheck.frontend

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.ActivityLevel
import com.frontend.nutricheck.client.model.data_sources.data.flags.Gender
import com.frontend.nutricheck.client.model.data_sources.data.flags.WeightGoal
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.repositories.user.UserDataRepository
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Calendar
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class OnboardingTest {

    @get:Rule(order = 0)
    val hilt = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val compose = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var dataStore : DataStore<Preferences>

    @Inject
    lateinit var userdataRepository : UserDataRepository

    @Before
    fun setUp() {
        hilt.inject()
        //clearOnboardingStatus()

    }
    private val newUsername = "Max Mustermann"
    private val validWeight = 75.0
    private val validHeight = 185.0
    private val invalidWeight = -5
    private val invalidHeight = 0
    private val birthYear = 1990
    private val birthdate = "01011990"

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
            .performTextInput(newUsername)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Fill in birthdate
        waitForBirthdateScreen()
        selectBirthdate()
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Select gender
        waitForGenderScreen()
        selectGender(Gender.MALE)
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Try invalid height
        waitForHeightScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_height))
            .performTextInput(invalidHeight.toString())
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_error_height_too_short))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_question_height)).assertIsDisplayed()

        // Fill in height
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_height))
            .performTextInput(validHeight.toString())
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()

        // Try invalid weight
        waitForWeightScreen()
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_weight))
            .performTextInput(invalidWeight.toString())
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_next)).performClick()
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_error_weight_too_low))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_question_weight)).assertIsDisplayed()
        compose.onNodeWithText(invalidWeight.toString()).performTextClearance()

        // Fill in weight
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_weight))
            .performTextInput(validWeight.toString())
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
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_target_weight))
            .performTextInput(validWeight.toString())
        compose.onNodeWithText(compose.activity.getString(R.string.onboarding_button_finish)).performClick()

        waitForDataToBeSaved()
        waitForDashboard()

        compose.onNodeWithContentDescription(SemanticsTags.BOTTOM_NAV_PROFILE).performClick()
        verifyUpdatedDataDisplayed()
    }
    private fun waitForDataToBeSaved() {
        compose.waitUntil(5_000) {
            runBlocking {
                val userData = userdataRepository.getUserData()
                userData.username == newUsername
            }
        }
    }

    private fun verifyUpdatedDataDisplayed() {
        compose.waitUntil(5_000) {
            // dark mode menu item is only displayed on profile page
            compose.onAllNodesWithText(compose.activity.getString(R.string.profile_menu_item_darkmode))
                .fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithText(compose.activity.getString(R.string.profile_name, newUsername)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.weight_kg, validWeight)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(R.string.height_cm, validHeight)).assertIsDisplayed()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val age = currentYear - birthYear
        compose.onNodeWithText(compose.activity.getString(R.string.age_years, age.toString())).assertIsDisplayed()
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

    private fun selectBirthdate() {
        compose.onNodeWithText(compose.activity.getString(R.string.userData_label_birthdate))
            .performClick()
        compose.waitUntil(5000) {
            compose.onAllNodesWithTag("DatePicker")
                .fetchSemanticsNodes().isNotEmpty()
        }
        // hardcoded because Strings is an internal class
        val datePickerEditButtonContentDescription = 2131624087
        compose.onNodeWithContentDescription(compose.activity.getString(datePickerEditButtonContentDescription))
            .performClick()

        // hardcoded because Strings is an internal class
        val datePickerInputLabel = 2131624076
        compose.onNodeWithText(compose.activity.getString(datePickerInputLabel)).performClick()
        compose.onNodeWithText(compose.activity.getString(datePickerInputLabel)).performTextInput(birthdate)

        compose.onNodeWithText(compose.activity.getString(R.string.save)).performClick()

        // Wait for the dialog to close
        compose.waitUntil(3000) {
            compose.onAllNodesWithText(compose.activity.getString(R.string.save))
                .fetchSemanticsNodes().isEmpty()
        }
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
//
//    private fun clearOnboardingStatus() = runBlocking{
//        val key = booleanPreferencesKey("onboarding_completed")
//        dataStore.edit { prefs ->
//            prefs.remove(key)
//        }
//    }
}