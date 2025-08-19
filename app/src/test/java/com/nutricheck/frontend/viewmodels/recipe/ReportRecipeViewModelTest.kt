package com.nutricheck.frontend.viewmodels.recipe

import android.content.Context
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.SnackbarManager
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeEvent
import com.frontend.nutricheck.client.ui.view_model.recipe.ReportRecipeViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.assertNull
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ReportRecipeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private lateinit var repository: RecipeRepository
    private lateinit var snackbarManager: SnackbarManager
    private lateinit var context: Context
    private val recipe = Recipe(
        id = "r1",
        name = "Pasta Pesto",
        calories = 300.0,
        carbohydrates = 40.0,
        protein = 15.0,
        fat = 10.0,
        servings = 2,
        instructions = "yummy",
        ingredients = listOf(
            Ingredient(
                recipeId = "r1",
                foodProduct = FoodProduct(
                    id = "fp1",
                    name = "Pasta",
                    calories = 200.0,
                    carbohydrates = 30.0,
                    protein = 10.0,
                    fat = 5.0,
                    servings = 1,
                    servingSize = ServingSize.ONEHOUNDREDGRAMS
                ),
                quantity = 100.0,
                servings = 1,
                servingSize = ServingSize.ONEHOUNDREDGRAMS
            ),
            Ingredient(
                recipeId = "r1",
                foodProduct = FoodProduct(
                    id = "fp2",
                    name = "Pesto Sauce",
                    calories = 100.0,
                    carbohydrates = 10.0,
                    protein = 5.0,
                    fat = 5.0,
                    servings = 1,
                    servingSize = ServingSize.ONEHOUNDREDGRAMS
                ),
                quantity = 50.0,
                servings = 5,
                servingSize = ServingSize.TENGRAMS
            )
        )
    )

    private fun makeViewModel() = ReportRecipeViewModel(repository, snackbarManager, context)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk(relaxed = true)
        snackbarManager = mockk(relaxed = true)
        context = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is empty and not reporting`() = testScope.runTest {
        val viewModel = makeViewModel()
        assertNull(viewModel.reportRecipeState.value.recipe)
        assertEquals("", viewModel.reportRecipeState.value.inputText)
        assertFalse(viewModel.reportRecipeState.value.reporting)
    }

    @Test
    fun `InputTextChanged updates inputText only`() = testScope.runTest {
        val viewModel = makeViewModel()

        viewModel.onEvent(ReportRecipeEvent.InputTextChanged("wrong calories"))
        advanceUntilIdle()

        val state = viewModel.reportRecipeState.value
        assertEquals("wrong calories", state.inputText)
        assertNull(state.recipe)
        assertFalse(state.reporting)
    }

    @Test
    fun `ReportClicked sets recipe and reporting`() = testScope.runTest {
        val viewModel = makeViewModel()

        viewModel.onEvent(ReportRecipeEvent.ReportClicked(recipe))
        advanceUntilIdle()

        val state = viewModel.reportRecipeState.value
        assertEquals("r1", state.recipe?.id)
        assertTrue(state.reporting)
    }

    @Test
    fun `DismissDialog clears recipe and inputText and sets reporting`() = testScope.runTest {
        val viewModel = makeViewModel()

        viewModel.onEvent(ReportRecipeEvent.ReportClicked(recipe))
        viewModel.onEvent(ReportRecipeEvent.InputTextChanged("text"))
        viewModel.onEvent(ReportRecipeEvent.DismissDialog)
        advanceUntilIdle()

        val state = viewModel.reportRecipeState.value
        assertNull(state.recipe)
        assertEquals("", state.inputText)
        assertFalse(state.reporting)
    }

    @Test
    fun `SendReport builds RecipeReport and calls repository then resets`() = testScope.runTest {
        val viewModel = makeViewModel()
        coEvery { repository.reportRecipe(any()) } returns Result.Success(mockk())

        viewModel.onEvent(ReportRecipeEvent.ReportClicked(recipe))
        viewModel.onEvent(ReportRecipeEvent.InputTextChanged("bad nutrition value"))
        viewModel.onEvent(ReportRecipeEvent.SendReport)
        advanceUntilIdle()

        coVerify {
            repository.reportRecipe(withArg { recipeReport ->
                assertEquals("r1", recipeReport.recipeId)
                assertEquals("Pasta Pesto", recipeReport.recipeName)
                assertEquals("yummy", recipeReport.recipeInstructions)
                assertEquals("bad nutrition value", recipeReport.description)
            })
        }

        val state = viewModel.reportRecipeState.value
        assertFalse(state.reporting)
        assertEquals("", state.inputText)
        assertNull(state.recipe)
    }

}