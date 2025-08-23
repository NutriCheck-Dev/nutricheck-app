package com.nutricheck.frontend

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.frontend.nutricheck.client.MainActivity
import com.frontend.nutricheck.client.R
import com.frontend.nutricheck.client.model.data_sources.persistence.LocalDatabase
import dagger.hilt.android.testing.HiltAndroidRule
import org.junit.runner.RunWith
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
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
        runCatching { db.clearAllTables() }
    }

    private fun openAddDialogThenRecipeEditor() {
        val bottomNavAddTag = compose.activity.getString(R.string.androidtest_tag_bottom_nav_add)
        val addDialogAddRecipeTag = compose.activity.getString(R.string.androidtest_tag_add_dialog_add_recipe)
        val recipeEditorTag = compose.activity.getString(R.string.androidtest_tag_recipe_editor_page)

        compose.onNodeWithTag(bottomNavAddTag)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithTag(addDialogAddRecipeTag)
            .assertIsDisplayed()
            .performClick()

        compose.onNodeWithTag(recipeEditorTag).assertIsDisplayed()
    }

    private fun addIngredientViaQuickAdd(query: String) {

    }
}