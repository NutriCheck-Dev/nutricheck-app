package com.nutricheck.frontend

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.frontend.nutricheck.client.ui.view_model.navigation.DiaryTab
import com.nutricheck.frontend.util.AndroidTestDataFactory.recipeToReportFactory
import com.nutricheck.frontend.util.BypassOnboardingRule
import com.nutricheck.frontend.util.SeedRemoteRecipeRule
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
class ReportRecipeTest {
    private val recipeName = "ReportDummyTest"

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val seedRemoteRecipe = SeedRemoteRecipeRule(
        context = ApplicationProvider.getApplicationContext(),
        buildRecipe = { recipeToReportFactory(recipeName) }
    )
    @get:Rule(order = 3) val compose = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var db: LocalDatabase

    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun reportRecipe() {
        val message = "This is a test report message."

        navigateToDiaryPageThenRecipePage()
        switchToOtherTabAndSearchRecipe(recipeName)
        openReportDialogAndEnterInput(recipeName, message)
        submitReportAndCheckDialogClosed()
    }

    private fun submitReportAndCheckDialogClosed() {
        compose.onNodeWithContentDescription(SemanticsTags.REPORT_DIALOG_CONFIRM)
            .assertIsDisplayed()
            .performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodes(
                hasContentDescription(SemanticsTags.REPORT_DIALOG),
                useUnmergedTree = true
            ).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun openReportDialogAndEnterInput(name: String, message: String) {
        val detailsButtonTag = SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX + name

        compose.waitForNode(hasContentDescription(detailsButtonTag))

        compose.onAllNodes(
            hasContentDescription(detailsButtonTag),
            useUnmergedTree = true
        ).onFirst()
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.DETAILS_MENU).assertIsDisplayed()

        val reportOptionTag = SemanticsTags.DETAILS_MENU_OPTION_PREFIX + DropdownMenuOptions.REPORT.toString()
        compose.onNodeWithContentDescription(reportOptionTag).assertIsDisplayed().performClick()

        compose.onNodeWithContentDescription(SemanticsTags.REPORT_DIALOG).assertIsDisplayed()

        compose.onNodeWithContentDescription(SemanticsTags.REPORT_DIALOG_INPUT).apply {
            assertIsDisplayed()
            performTextClearance()
            performTextInput(message)
        }
    }

    private fun switchToOtherTabAndSearchRecipe(query: String) {
        compose.onAllNodes(
            hasContentDescriptionPrefix(SemanticsTags.RECIPE_PAGE_TAB_PREFIX),
            useUnmergedTree = true
        )[1]
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_QUERY).apply {
            assertIsDisplayed()
            performTextClearance()
            performTextInput(query)
        }

        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_BUTTON).performClick()
    }

    private fun navigateToDiaryPageThenRecipePage() {
        compose.onNodeWithContentDescription(SemanticsTags.BOTTOM_NAV_DIARY_PAGE).performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodes(
                hasContentDescriptionPrefix(SemanticsTags.OVERVIEW_SWITCHER_TAB_PREFIX),
                useUnmergedTree = true
            ).fetchSemanticsNodes().isNotEmpty()
        }

        val recipesTab = SemanticsTags.OVERVIEW_SWITCHER_TAB_PREFIX + DiaryTab.RECIPES.name
        compose.onNode(
            hasContentDescription(recipesTab),
            useUnmergedTree = true
        ).assertIsDisplayed().performClick()

        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_PAGE).assertIsDisplayed()
    }

    private fun hasContentDescriptionPrefix(prefix: String) : SemanticsMatcher =
        SemanticsMatcher("TestTag startsWith($prefix)") { node ->
            val list = node.config.getOrNull(SemanticsProperties.ContentDescription)
            list?.firstOrNull()?.startsWith(prefix) == true
        }

    private fun ComposeContentTestRule.waitForNode(
        matcher: SemanticsMatcher,
        useUnmergedTree: Boolean = true,
        timeoutMs: Long = 10_000
    ) {
        waitUntil(timeoutMs) {
            onAllNodes(matcher, useUnmergedTree).fetchSemanticsNodes().isNotEmpty()
        }
    }
}