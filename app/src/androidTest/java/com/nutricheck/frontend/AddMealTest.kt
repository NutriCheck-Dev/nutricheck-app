package com.nutricheck.frontend

import android.Manifest
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.rule.GrantPermissionRule
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
 * Test case: Add meal
 * Variants:
 * 1) Search for a food item and add it to the meal
 * 2) Scan a meal (only error handling, as UI tests can't handle camera input)
 * 3) Create a recipe and add it to the meal
 */
@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class AddMealTest {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val compose = createAndroidComposeRule<MainActivity>()
    @get:Rule(order = 3)
    val cameraPermission: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.CAMERA)

    @Inject lateinit var db: LocalDatabase

    @Before fun setUp() {
        hilt.inject()
    }

    @After fun tearDown() {
        db.close()
    }

    @Test
    fun addMeal_manual_online_updatesOverview() {
        val firstIngredient = "Apfel"
        val secondIngredient = "Banane"
        openAddDialog()
        openAddMeal()

        openIngredientSearch()
        addMealIngredientViaQuickAdd(firstIngredient)
        addMealIngredientViaQuickAdd(secondIngredient)

        selectDayTime()
        persistMeal()

        assertOnHistoryPage()
        val itemCount = compose.onAllNodes(
            hasContentDescriptionPrefix(SemanticsTags.MEAL_FOOD_ITEM_PREFIX),
            useUnmergedTree = true
        ).fetchSemanticsNodes().size

        assert(itemCount == 2)
    }

    @Test
    fun addMeal_scan_online_updatesOverview() {
        openAddDialog()
        tapScanMeal()

        checkScanErrorDisplayed()
    }


    @Test
    fun addMeal_ownRecipe_offline_updatesOverview() {
        val recipeName = "My Awesome Recipe"
        val firstIngredient = "Apfel"
        val secondIngredient = "Banane"
        openAddDialogThenRecipeEditor()
        openIngredientSearch()
        addRecipeIngredientViaQuickAdd(firstIngredient)
        addRecipeIngredientViaQuickAdd(secondIngredient)
        fillAndPersistRecipe(name = recipeName, description = "This is my awesome recipe.")
        assertOnRecipePage(name = recipeName)

        openAddDialog()
        openAddMeal()

        openIngredientSearch()
        compose.onAllNodes(
            hasContentDescriptionPrefix(SemanticsTags.RECIPE_PAGE_TAB_PREFIX),
            useUnmergedTree = true
        )[1]
            .assertIsDisplayed()
            .performClick()
        addMealIngredientViaQuickAdd(recipeName)
        selectDayTime()
        persistMeal()
        assertOnHistoryPage(recipeName)
        val itemCount = compose.onAllNodes(
            hasContentDescriptionPrefix(SemanticsTags.MEAL_RECIPE_ITEM_PREFIX),
            useUnmergedTree = true
        ).fetchSemanticsNodes().size

        assert(itemCount == 1)
    }

    private fun openAddDialog() {
        compose.onNodeWithContentDescription(SemanticsTags.BOTTOM_NAV_ADD)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.ADD_DIALOG)
            .assertIsDisplayed()
    }

    private fun openAddMeal() {
        compose.onNodeWithContentDescription(SemanticsTags.ADD_DIALOG_ADD_MEAL)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.MEAL_EDITOR_PAGE)
            .assertIsDisplayed()
    }

    private fun tapScanMeal() {
        compose.onNodeWithContentDescription(SemanticsTags.ADD_DIALOG_SCAN_MEAL)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithTag(SemanticsTags.MEAL_SCAN_TAKE_PHOTO)
            .performClick()

        compose.waitUntil(5_000) {
            compose.onAllNodesWithContentDescription(SemanticsTags.MEAL_SCAN_SEND_PHOTO)
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription(SemanticsTags.MEAL_SCAN_SEND_PHOTO)
            .assertIsDisplayed()
            .performClick()
    }
    private fun checkScanErrorDisplayed() {
        val expectedError = SemanticsTags.MEAL_SCAN_ERROR_DIALOG
        compose.waitUntil(20_000) {
            compose.onAllNodes(hasContentDescription(expectedError))
                .fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithContentDescription(expectedError).assertIsDisplayed()
    }
    private fun openIngredientSearch() {
        compose.onNodeWithContentDescription(SemanticsTags.FOODCOMPONENT_LIST_ADD_BUTTON)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_SHEET)
            .assertIsDisplayed()
    }

    private fun addMealIngredientViaQuickAdd(query: String) {
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_QUERY).performTextClearance()
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_QUERY).performTextInput(query)
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_BUTTON).performClick()

        compose.waitUntil(30_000) {
            compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.MEAL_SEARCH_PREFIX))
                .fetchSemanticsNodes().isNotEmpty()
        }

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.MEAL_SEARCH_PREFIX))
            .onFirst()
            .performClick()
    }
    private fun addRecipeIngredientViaQuickAdd(query: String) {
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_QUERY).performTextClearance()
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_QUERY).performTextInput(query)
        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_BUTTON).performClick()

        compose.waitUntil(30_000) {
            compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_ADD_BUTTON_PREFIX))
                .fetchSemanticsNodes().isNotEmpty()
        }

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_ADD_BUTTON_PREFIX))
            .onFirst()
            .performClick()
    }

    private fun selectDayTime() {
        compose.onNodeWithContentDescription(SemanticsTags.DAYTIME_PICKER)
            .assertExists()
            .performClick()


        compose.waitUntil(5_000) {
            compose.onAllNodes(
                hasContentDescriptionPrefix(SemanticsTags.DAYTIME_ITEM_PREFIX),
                useUnmergedTree = true
            ).fetchSemanticsNodes().isNotEmpty()
        }

        compose.onNodeWithTag("${SemanticsTags.DAYTIME_ITEM_PREFIX}LUNCH")
            .assertExists()
            .assertHasClickAction()
            .performClick()

        compose.mainClock.advanceTimeBy(500)
    }


    private fun persistMeal() {
        compose.onNodeWithContentDescription(SemanticsTags.MEAL_EDITOR_PERSIST)
            .performClick()
        compose.mainClock.advanceTimeBy(1000)
    }
    private fun assertOnHistoryPage() {
        compose.onNodeWithContentDescription(SemanticsTags.HISTORY_PAGE).assertIsDisplayed()
    }
    private fun openAddDialogThenRecipeEditor() {

        compose.onNodeWithContentDescription(SemanticsTags.BOTTOM_NAV_ADD)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.ADD_DIALOG_ADD_RECIPE)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_PAGE).assertIsDisplayed()
    }
    private fun fillAndPersistRecipe(name: String, description: String) {
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_NAME).performTextClearance()
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_NAME).performTextInput(name)
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_DESCRIPTION).performTextClearance()
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_DESCRIPTION).performTextInput(description)
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_PERSIST).performClick()
    }
    private fun assertOnRecipePage(name: String) {
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_PAGE).assertIsDisplayed()
        compose.onNodeWithText(name, substring = false).assertIsDisplayed()
    }

    private fun assertOnHistoryPage(name: String) {
        compose.onNodeWithContentDescription(SemanticsTags.HISTORY_PAGE).assertIsDisplayed()
        compose.onNodeWithText(name, substring = false).assertIsDisplayed()
    }

    private fun hasContentDescriptionPrefix(prefix: String): SemanticsMatcher =
        SemanticsMatcher("ContentDescription startsWith($prefix)") { node ->
            val list = node.config.getOrNull(SemanticsProperties.ContentDescription)
            list?.firstOrNull()?.startsWith(prefix) == true
        }
}

