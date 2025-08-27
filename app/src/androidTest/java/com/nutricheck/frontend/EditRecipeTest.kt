package com.nutricheck.frontend

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasContentDescription
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
import com.nutricheck.frontend.util.AndroidTestDataFactory.ownerRecipeFactory
import com.nutricheck.frontend.util.BypassOnboardingRule
import com.nutricheck.frontend.util.DbPersistRule
import com.nutricheck.frontend.util.SeedOwnerRecipeRule
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
class EditRecipeTest {

    val name = "Pasta Pesto"

    @get:Rule(order = -1) val dbPersist = DbPersistRule()
    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val bypassOnboarding = BypassOnboardingRule(
        ApplicationProvider.getApplicationContext()
    )
    @get:Rule(order = 2) val seedOwnerRecipe =
        SeedOwnerRecipeRule(buildRecipe = ownerRecipeFactory(name))
    @get:Rule(order = 3) val compose = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var db: LocalDatabase

    @Before
    fun setUp() {
        hilt.inject()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun editRecipe_changeName_persisted() {
        val newName = "Pasta Pomodoro"

        navigateToDiaryPageThenRecipePage()
        navigateToRecipeEditor()
        editRecipeAndPersist(newName)
    }

    private fun editRecipeAndPersist(newName: String) {
        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_PAGE).assertIsDisplayed()

        val nameField = SemanticsTags.RECIPE_EDITOR_NAME
        compose.onNodeWithContentDescription(nameField).assertIsDisplayed().performClick()
        compose.onNodeWithContentDescription(nameField).performTextClearance()
        compose.onNodeWithContentDescription(nameField).performTextInput(newName)

        val saveButton = SemanticsTags.RECIPE_EDITOR_PERSIST
        compose.onNodeWithContentDescription(saveButton).assertIsDisplayed().performClick()

        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_PAGE).assertIsDisplayed()
        compose.onNodeWithContentDescription(SemanticsTags.DISHITEM_PREFIX + newName).assertIsDisplayed()
    }

    private fun navigateToRecipeEditor() {
        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX))
            .fetchSemanticsNodes().isNotEmpty()

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX))
            .onFirst()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.DETAILS_MENU).assertIsDisplayed()

        val editOptionTag = SemanticsTags.DETAILS_MENU_OPTION_PREFIX + DropdownMenuOptions.EDIT.toString()
        compose.onNodeWithContentDescription(editOptionTag).assertIsDisplayed().performClick()

        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_EDITOR_PAGE).assertIsDisplayed()
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

        val recipeTag = SemanticsTags.DISHITEM_PREFIX + name
        compose.onNodeWithContentDescription(recipeTag).assertIsDisplayed()
    }

    private fun hasContentDescriptionPrefix(prefix: String) : SemanticsMatcher =
        SemanticsMatcher("TestTag startsWith($prefix)") { node ->
            val list = node.config.getOrNull(SemanticsProperties.ContentDescription)
            list?.firstOrNull()?.startsWith(prefix) == true
        }
}