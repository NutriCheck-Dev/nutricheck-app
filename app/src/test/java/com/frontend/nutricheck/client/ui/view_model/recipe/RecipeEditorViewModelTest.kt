package com.frontend.nutricheck.client.ui.view_model.recipe

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.frontend.nutricheck.client.model.data_sources.data.FoodProduct
import com.frontend.nutricheck.client.model.data_sources.data.Ingredient
import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.model.data_sources.data.Result
import com.frontend.nutricheck.client.model.data_sources.data.flags.RecipeVisibility
import com.frontend.nutricheck.client.model.data_sources.data.flags.ServingSize
import com.frontend.nutricheck.client.model.repositories.appSetting.AppSettingRepository
import com.frontend.nutricheck.client.model.repositories.foodproducts.FoodProductRepository
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel
import com.frontend.nutricheck.client.ui.view_model.utils.CombinedSearchListStore
import com.frontend.nutricheck.client.ui.view_model.snackbar.SnackbarManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class RecipeEditorViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val testScope = TestScope(dispatcher)

    private lateinit var recipeRepository: RecipeRepository
    private lateinit var appSettings: AppSettingRepository
    private lateinit var foodProductRepository: FoodProductRepository
    private lateinit var snackbarManager: SnackbarManager
    private lateinit var combinedSearchListStore: CombinedSearchListStore
    private lateinit var context: Context

    private val foodProduct1 = FoodProduct(
        id = "fp1",
        name = "Pasta",
        calories = 200.0,
        carbohydrates = 30.0,
        protein = 10.0,
        fat = 5.0,
        servings = 1.0,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private val foodProduct2 = FoodProduct(
        id = "fp2",
        name = "Pesto Sauce",
        calories = 100.0,
        carbohydrates = 10.0,
        protein = 5.0,
        fat = 5.0,
        servings = 1.0,
        servingSize = ServingSize.ONEHOUNDREDGRAMS
    )

    private fun makeCreateViewModel(): RecipeEditorViewModel =
        RecipeEditorViewModel(
            recipeRepository,
            appSettings,
            foodProductRepository,
            snackbarManager,
            combinedSearchListStore,
            context,
            SavedStateHandle()
        )

    private fun makeEditViewModel(recipeId: String): RecipeEditorViewModel =
        RecipeEditorViewModel(
            recipeRepository,
            appSettings,
            foodProductRepository,
            snackbarManager,
            combinedSearchListStore,
            context,
            SavedStateHandle(mapOf("recipeId" to recipeId))
        )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)

        recipeRepository = mockk(relaxed = true)
        appSettings = mockk(relaxed = true)
        foodProductRepository = mockk(relaxed = true)
        snackbarManager = mockk(relaxed = true)
        combinedSearchListStore = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { context.getString(any()) } returns "error"
        every { appSettings.language } returns emptyFlow()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial draft in Create mode`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        val draft = createViewModel.draft.value
        assertNull(draft.original)
        assertTrue(draft.ingredients.isEmpty())
        assertEquals(1.0, draft.servings, 0.0)
        assertTrue(draft.ingredients.isEmpty())
    }

    @Test
    fun `confirm changed title, description, servings, query, expand`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()

        createViewModel.onEvent(RecipeEditorEvent.TitleChanged("Pasta Pesto"))
        createViewModel.onEvent(RecipeEditorEvent.DescriptionChanged("Boil Pasta, mix with"))
        createViewModel.onEvent(RecipeEditorEvent.ServingsChanged(3.0))
        createViewModel.onEvent(RecipeEditorEvent.QueryChanged("tomato"))
        createViewModel.onEvent(RecipeEditorEvent.ShowBottomSheet)
        advanceUntilIdle()

        val draft = createViewModel.draft.value
        assertEquals("Pasta Pesto", draft.title)
        assertEquals("Boil Pasta, mix with", draft.description)
        assertEquals(3.0, draft.servings, 0.0)
        assertEquals("tomato", draft.query)
        assertTrue(draft.expanded)
    }

    @Test
    fun `IngredientAdded adds or replaces, updates store and emits event`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()

        val awaited = async { createViewModel.events.first() }
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct1))
        advanceUntilIdle()

        val draft1 = createViewModel.draft.value
        assertEquals(listOf("fp1"), draft1.ingredients.map { it.id })
        assertTrue(draft1.results.none { it.id == "fp1" })
        verify { combinedSearchListStore.update(match {
            it.any { component -> component.id == "fp1" } }) }

        val foodProduct1b = foodProduct1.copy(servings = 2.0)
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct1b))
        advanceUntilIdle()

        val draft2 = createViewModel.draft.value
        assertEquals(1, draft2.ingredients.size)
        assertEquals(2.0, (draft2.ingredients.first() as FoodProduct).servings, 0.0)
        assertTrue(awaited.await() is RecipeEditorEvent.IngredientAdded)
    }

    @Test
    fun `IngredientRemoved moves item to results and updates store`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct2))
        advanceUntilIdle()

        createViewModel.onEvent(RecipeEditorEvent.IngredientRemoved(foodProduct2))
        advanceUntilIdle()

        val draft = createViewModel.draft.value
        assertTrue(draft.ingredients.none { it.id == "fp2" })
        assertTrue(draft.results.any { it.id == "fp2" })
        verify { combinedSearchListStore.update(
            match { list -> list.any { it.id == "fp2" } }) }
    }

    @Test
    fun `SearchIngredients does nothing for blank query`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.SearchIngredients)
        advanceUntilIdle()
        coVerify(exactly = 0) { foodProductRepository.searchFoodProducts(any(), any()) }
    }

    @Test
    fun `SearchIngredients success updates results and store`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.QueryChanged("pasta"))
        advanceUntilIdle()

        coEvery { foodProductRepository.searchFoodProducts("pasta", any()) } returns flowOf(Result.Success(listOf(foodProduct1, foodProduct2)))

        createViewModel.onEvent(RecipeEditorEvent.SearchIngredients)
        advanceUntilIdle()

        val draft = createViewModel.draft.value
        assertEquals(listOf("fp1", "fp2"), draft.results.map { it.id })
        assertTrue(draft.hasSearched)
        assertEquals("pasta", draft.lastSearchedQuery)
        verify { combinedSearchListStore.update(
            match { it.map { component -> component.id }.containsAll(listOf("fp1", "fp2")) }
        ) }
    }

    @Test
    fun `SearchIngredients error sets error`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.QueryChanged("x"))
        advanceUntilIdle()

        coEvery { foodProductRepository.searchFoodProducts("x", any()) } returns flowOf(Result.Error(500, "error"))

        createViewModel.onEvent(RecipeEditorEvent.SearchIngredients)
        advanceUntilIdle()

        assertTrue(createViewModel.uiState.value is BaseViewModel.UiState.Error)
    }

    @Test
    fun `SaveRecipe with bank title sets error`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct1))
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct2))
        createViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
        advanceUntilIdle()

        assertTrue(createViewModel.uiState.value is BaseViewModel.UiState.Error)
        coVerify(exactly = 0) { recipeRepository.insertRecipe(any()) }
    }

    @Test
    fun `SaveRecipe with less than two ingredients sets error`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.TitleChanged("Pasta Pesto"))
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct1))
        createViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
        advanceUntilIdle()

        assertTrue(createViewModel.uiState.value is BaseViewModel.UiState.Error)
        coVerify(exactly = 0) { recipeRepository.insertRecipe(any()) }
    }

    @Test
    fun `SaveRecipe in Create mode inserts recipe and resets`() = testScope.runTest {
        val createViewModel = makeCreateViewModel()
        createViewModel.onEvent(RecipeEditorEvent.TitleChanged("Pasta Pesto"))
        createViewModel.onEvent(RecipeEditorEvent.DescriptionChanged("yummy"))
        createViewModel.onEvent(RecipeEditorEvent.ServingsChanged(2.0))
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct1))
        createViewModel.onEvent(RecipeEditorEvent.IngredientAdded(foodProduct2))
        advanceUntilIdle()

        val awaited = async { createViewModel.events.first() }
        createViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
        advanceUntilIdle()

        coVerify {
            recipeRepository.insertRecipe(withArg { recipe ->
                assertEquals("Pasta Pesto", recipe.name)
                assertEquals(200.0 * 1 + 100.0 * 1, recipe.calories, 0.0)
                assertEquals(30.0 * 1 + 10.0 * 1, recipe.carbohydrates, 0.0)
                assertEquals(10.0 * 1 + 5.0 * 1, recipe.protein, 0.0)
                assertEquals(5.0 * 1 + 5.0 * 1, recipe.fat, 0.0)
                assertEquals(2.0, recipe.servings, 0.0)
                assertEquals(2, recipe.ingredients.size)
                assertEquals(RecipeVisibility.OWNER, recipe.visibility)
            })
        }

        assertTrue(awaited.await() is RecipeEditorEvent.RecipeSaved)
    }

    @Test
    fun `Edit mode preloads recipe and SaveRecipe updates`() = testScope.runTest {
        val existing = Recipe(
            id = "r1",
            name = "recipe1",
            instructions = "old",
            calories = 300.0, carbohydrates = 40.0, protein = 15.0, fat = 10.0,
            servings = 1.0,
            ingredients = listOf(
                Ingredient(recipeId = "r1", foodProduct = foodProduct1, servings = 1.0, servingSize = foodProduct1.servingSize),
                Ingredient(recipeId = "r1", foodProduct = foodProduct2, servings = 1.0, servingSize = foodProduct2.servingSize)
            ),
            visibility = RecipeVisibility.OWNER
        )
        coEvery { recipeRepository.getRecipeById("r1") } returns existing

        val editViewModel = makeEditViewModel(existing.id)
        advanceUntilIdle()

        editViewModel.onEvent(RecipeEditorEvent.TitleChanged("New Title"))
        editViewModel.onEvent(RecipeEditorEvent.ServingsChanged(3.0))
        advanceUntilIdle()

        val awaited = async { editViewModel.events.first() }
        editViewModel.onEvent(RecipeEditorEvent.SaveRecipe)
        advanceUntilIdle()

        coVerify {
            recipeRepository.updateRecipe(withArg { recipe ->
                assertEquals("r1", recipe.id)
                assertEquals("New Title", recipe.name)
                assertEquals(3.0, recipe.servings, 0.0)
                assertEquals(2, recipe.ingredients.size)
            })
        }
        assertTrue(awaited.await() is RecipeEditorEvent.RecipeSaved)
    }

    @Test
    fun `Clear resets search state and clears error`() = testScope.runTest {
        val viewModel = makeCreateViewModel()

        viewModel.onEvent(RecipeEditorEvent.QueryChanged("pasta"))
        coEvery { foodProductRepository.searchFoodProducts("pasta", any()) } returns
                flowOf(Result.Success(listOf(foodProduct1, foodProduct2)))

        viewModel.onEvent(RecipeEditorEvent.SearchIngredients)
        advanceUntilIdle()

        val before = viewModel.draft.value
        assertEquals("pasta", before.query)
        assertTrue(before.hasSearched)
        assertEquals("pasta", before.lastSearchedQuery)
        assertEquals(listOf("fp1", "fp2"), before.results.map { it.id })

        viewModel.onEvent(RecipeEditorEvent.Clear)
        advanceUntilIdle()

        val after = viewModel.draft.value
        assertEquals("", after.query)
        assertFalse(after.hasSearched)
        assertNull(after.lastSearchedQuery)

        assertFalse(viewModel.uiState.value is BaseViewModel.UiState.Error)
    }

}