package com.nutricheck.frontend

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assertContentDescriptionContains
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.nutricheck.frontend.util.BypassOnboardingRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

/**
 * Test case: Change weight history chart interval
 * Variants:
 * 1) User selects 1 month
 * 2) User selects 6 months
 * 3) User selects 12 months
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class WeightHistoryDiagramTest {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val compose = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var db: LocalDatabase

    @Before fun setUp() { hilt.inject() }

    @After fun tearDown() { db.close() }

    @Test
    fun interval1Month_setsCorrectRange() {
        selectInterval(SemanticsTags.WEIGHT_HISTORY_RANGE_SELECTOR + SemanticsTags.DIAGRAM_FIRST_INTERVAL)
        waitForChartUpdate(SemanticsTags.RANGE_WEIGHT_LAST_1_MONTH)
        assertChartInterval(SemanticsTags.RANGE_WEIGHT_LAST_1_MONTH)
    }

    @Test
    fun interval6Months_setsCorrectRange() {
        selectInterval(SemanticsTags.WEIGHT_HISTORY_RANGE_SELECTOR + SemanticsTags.DIAGRAM_SECOND_INTERVAL)
        waitForChartUpdate(SemanticsTags.RANGE_WEIGHT_LAST_6_MONTHS)
        assertChartInterval(SemanticsTags.RANGE_WEIGHT_LAST_6_MONTHS)
    }

    @Test
    fun interval12Months_setsCorrectRange() {
        selectInterval(SemanticsTags.WEIGHT_HISTORY_RANGE_SELECTOR + SemanticsTags.DIAGRAM_THIRD_INTERVAL)
        waitForChartUpdate(SemanticsTags.RANGE_WEIGHT_LAST_12_MONTHS)
        assertChartInterval(SemanticsTags.RANGE_WEIGHT_LAST_12_MONTHS)
    }


    // -------- Helper-Funktionen --------

    private fun selectInterval(tag: String) {
        compose.onNodeWithTag(tag)
            .assertIsDisplayed()
            .performClick()
    }

    private fun waitForChartUpdate(expectedTag: String, timeoutMillis: Long = 4000) {
        compose.waitUntil(timeoutMillis) {
            val node = compose.onNodeWithTag(SemanticsTags.WEIGHT_HISTORY_CHART).fetchSemanticsNode()
            val contentDesc = node.config.getOrNull(SemanticsProperties.ContentDescription)
            contentDesc?.firstOrNull() == expectedTag
        }
    }

    private fun assertChartInterval(expectedTag: String) {
        compose.onNodeWithTag(SemanticsTags.WEIGHT_HISTORY_CHART)
            .assertIsDisplayed()
            .assertContentDescriptionContains(expectedTag)
    }
}
