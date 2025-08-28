package com.nutricheck.frontend

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.nutricheck.frontend.util.BypassOnboardingRule
import com.nutricheck.frontend.util.DbPersistRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
class ChangePersonalDataTest {

    @get:Rule(order = -1) val dbPersist = DbPersistRule()
    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val compose = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var db: LocalDatabase

    private val originalWeight = 80.0
    private val originalHeight = 180.0
    private val newHeight = 185.0
    private val newWeight = 75.0

    @Before
    fun setUp() {
        hilt.inject()
        // used UserData from DbPersistRule (backup)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun changePersonalData_heightChange_displaysUpdatedValues() {
        compose.onNodeWithContentDescription(SemanticsTags.BOTTOM_NAV_PROFILE).performClick()
        verifyOriginalDataDisplayed()
        compose.onNodeWithContentDescription(compose.activity.getString(
            R.string.profile_menu_item_personal_data)).performClick()
        compose.waitUntil(5_000) {
            // username label is only displayed on personal data page
            compose.onAllNodesWithText(compose.activity.getString(R.string.userData_label_name))
                .fetchSemanticsNodes().isNotEmpty()
        }

        changeHeightValue(newHeight)
        changeWeightValue(newWeight)
        compose.onNodeWithContentDescription(SemanticsTags.PERSONAL_DATA_SAFE_BUTTON).performClick()

        verifyUpdatedDataDisplayed()
    }

    private fun verifyOriginalDataDisplayed() {
        compose.waitUntil(5_000) {
            // dark mode menu item is only displayed on profile page
            compose.onAllNodesWithText(compose.activity.getString(R.string.profile_menu_item_darkmode))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription(SemanticsTags.PROFILE_DATA_WEIGHT)
            .assertTextEquals("$originalWeight kg")

        compose.onNodeWithContentDescription(SemanticsTags.PROFILE_DATA_HEIGHT)
            .assertTextEquals("$originalHeight cm")
    }
    private fun verifyUpdatedDataDisplayed() {
        compose.waitUntil(5_000) {
            // dark mode menu item is only displayed on profile page
            compose.onAllNodesWithText(compose.activity.getString(R.string.profile_menu_item_darkmode))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription(SemanticsTags.PROFILE_DATA_WEIGHT)
            .assertTextEquals("$newWeight kg")

        compose.onNodeWithContentDescription(SemanticsTags.PROFILE_DATA_HEIGHT)
            .assertTextEquals("$newHeight cm")
    }

    private fun changeHeightValue(newValue: Double) {
        compose.onNodeWithText(originalHeight.toString()).performClick()
        compose.onNodeWithText(originalHeight.toString()).performTextClearance()
        compose.onNodeWithText("").performTextInput(newValue.toString())
    }

    private fun changeWeightValue(newValue: Double) {
        compose.onNodeWithText(originalWeight.toString()).performClick()
        compose.onNodeWithText(originalWeight.toString()).performTextClearance()
        compose.onNodeWithText("").performTextInput(newValue.toString())
    }
}