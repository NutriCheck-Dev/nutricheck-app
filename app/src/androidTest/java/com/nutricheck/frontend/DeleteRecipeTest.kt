package com.nutricheck.frontend

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.model.data_sources.data.flags.DropdownMenuOptions
import com.frontend.nutricheck.client.model.data_sources.data.flags.SemanticsTags
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import com.frontend.nutricheck.client.ui.view_model.navigation.DiaryTab
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
class DeleteRecipeTest {

    @get:Rule(order = -1) val dbPersist = DbPersistRule()
    @get:Rule(order = 0) val hilt = HiltAndroidRule(this)
    @get:Rule(order = 1) val compose = createAndroidComposeRule<MainActivity>()

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
    fun deleteRecipe() {
        navigateToDiaryPageThenRecipePage()

        val recipeName = "My Awesome Recipe"

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX))
            .fetchSemanticsNodes().isNotEmpty()

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX))
            .onFirst()
            .performClick()

        compose.onNodeWithContentDescription(SemanticsTags.DETAILS_MENU).assertIsDisplayed()

        val deleteOptionTag = SemanticsTags.DETAILS_MENU_OPTION_PREFIX + DropdownMenuOptions.DELETE.toString()
        compose.onNodeWithContentDescription(deleteOptionTag).assertIsDisplayed().performClick()

//        val deleteMessage = compose.activity.getString(R.string.snackbar_message_recipe_deleted)
//        compose.waitUntil(3_000) {
//            compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.SNACKBAR))
//                .fetchSemanticsNodes()
//                .any { node ->
//                    val list = node.config.getOrNull(SemanticsProperties.ContentDescription)
//                    list?.firstOrNull()?.contains(deleteMessage) == true
//                }
//        }

        compose.onAllNodes(hasContentDescriptionPrefix(SemanticsTags.DISHITEM_DETAILS_BUTTON_PREFIX))
            .fetchSemanticsNodes().isEmpty()


    }


    private fun navigateToDiaryPageThenRecipePage() {
        compose.onNodeWithContentDescription(SemanticsTags.DIARY_PAGE).performClick()

        val recipesTabDescription = SemanticsTags.OVERVIEW_SWITCHER_TAB_PREFIX + DiaryTab.RECIPES.name
        compose.onNode(
            hasContentDescriptionPrefix(recipesTabDescription),
            useUnmergedTree = true
        ).assertIsDisplayed().performClick()

        compose.onNodeWithContentDescription(SemanticsTags.RECIPE_PAGE).assertIsDisplayed()
    }

    private fun hasContentDescriptionPrefix(prefix: String) : SemanticsMatcher =
        SemanticsMatcher("TestTag startsWith($prefix)") { node ->
            val list = node.config.getOrNull(SemanticsProperties.ContentDescription)
            list?.firstOrNull()?.startsWith(prefix) == true
        }
}