package com.nutricheck.frontend

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@HiltAndroidTest
@RunWith(AndroidJUnit4::class)
@LargeTest
class CreateRecipeTest {

    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()

    @Inject lateinit var db: LocalDatabase

    @Before fun setUp() {
        hilt.inject()
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            LocalDatabase::class.java
        ).build()
    }

    @Test
    fun createRecipe_viaQuickAdd() {
        val recipeName = "My Awesome Recipe"

        openAddDialogThenRecipeEditor()
        openIngredientSearch()
//        addIngredientViaQuickAdd("apple")
//        addIngredientViaQuickAdd("banana")
//        fillAndPersistRecipe(name = recipeName, description = "This is my awesome recipe.")
//        assertOnRecipePage(name = recipeName)
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
            compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_ADD_PREFIX))
                .fetchSemanticsNodes().isNotEmpty()
        }

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_ADD_PREFIX))
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
}