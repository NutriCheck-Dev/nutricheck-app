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
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.nutricheck.frontend.util.BypassOnboardingRule
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.apache.commons.lang3.RandomStringUtils
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class UploadRecipeTest {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val compose = createAndroidComposeRule<MainActivity>()

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
    fun createAndUploadRecipe() {
        val recipeName = RandomStringUtils.insecure().nextAlphabetic(8)
        createRecipe_viaQuickAdd(recipeName)
        uploadRecipe(recipeName)
        checkIfRecipeIsUploaded(recipeName)
    }

    private fun checkIfRecipeIsUploaded(recipeName: String) {
        switchToOtherTabAndSearchRecipe(recipeName)
        val dishItemTag = SemanticsTags.DISHITEM_PREFIX + recipeName
        compose.waitForNode(hasContentDescription(dishItemTag))
        compose.onAllNodes(hasContentDescription(dishItemTag), useUnmergedTree = true)
            .onFirst()
            .assertIsDisplayed()
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

    private fun uploadRecipe(recipeName: String) {
        val detailsButtonTag = SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX + recipeName
        compose.waitForNode(hasContentDescriptionPrefix(detailsButtonTag))

        compose.onAllNodes(
            hasContentDescription(detailsButtonTag),
            useUnmergedTree = true
        ).onFirst()
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.DETAILS_MENU).assertIsDisplayed()

        val uploadOptionTag = SemanticsTags.DETAILS_MENU_OPTION_PREFIX + DropdownMenuOptions.UPLOAD.toString()
        compose.onNodeWithContentDescription(uploadOptionTag).assertIsDisplayed().performClick()

    }

    private fun createRecipe_viaQuickAdd(recipeName: String) {
        openAddDialogThenRecipeEditor()
        openIngredientSearch()
        addIngredientViaQuickAdd("Apfel")
        addIngredientViaQuickAdd("Sushi")
        fillAndPersistRecipe(name = recipeName, description = "This is my awesome recipe.")
        assertOnRecipePage(name = recipeName)
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

    private fun openIngredientSearch() {
        compose.onNodeWithContentDescription(SemanticsTags.FOODCOMPONENT_LIST_ADD_BUTTON)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.SEARCH_SHEET)
            .assertIsDisplayed()
    }

    private fun addIngredientViaQuickAdd(query: String) {
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