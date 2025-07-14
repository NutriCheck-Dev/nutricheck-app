package com.frontend.nutricheck.client.ui.view_model.add_components

import com.frontend.nutricheck.client.model.data_sources.data.FoodComponent
import com.frontend.nutricheck.client.model.repositories.recipe.RecipeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow

data class AddMealState(
    val foodProducts: List<FoodComponent> = emptyList(), // Replace String with actual FoodProduct type
    val mealName: String = "",
    val currentSegment: String = "All",
)

sealed interface AddMealEvent {
    data class AddFoodProduct(val foodId: String) : AddMealEvent
    data class SaveMeal(val mealName: String) : AddMealEvent
    data class SaveAsRecipe(val recipeName: String) : AddMealEvent
    data object DisplayMealDetails : AddMealEvent
    data class FoodComponentClick(val foodId: String) : AddMealEvent
    data object MyRecipesClick : AddMealEvent
    data object AddClick : AddMealEvent
}

@HiltViewModel
class AddMealViewModel @Inject constructor(
    private val recipeRepository: RecipeRepository
) : BaseAddMealViewModel() {
    private val _addMealState = MutableStateFlow(AddMealState())
    val createRecipeState = _addMealState.asStateFlow()

    val _events = MutableSharedFlow<AddMealEvent>()
    val events: SharedFlow<AddMealEvent> = _events.asSharedFlow()


    fun onEvent(event: AddMealEvent) {}

    override fun onAddFoodProductClicked() {
        TODO("Not yet implemented")
    }

    override fun saveMeal() {
        TODO("Not yet implemented")
    }

    override fun saveAsRecipe() {
        TODO("Not yet implemented")
    }

    override fun displayMealDetails() {
        TODO("Not yet implemented")
    }
    override fun onFoodComponentClick(foodId: String) {
        TODO("Not yet implemented")
    }
    override fun onMyRecipesClick() {
        TODO("Not yet implemented")
    }
    override fun onAddClick() {
        TODO("Not yet implemented")
    }
}