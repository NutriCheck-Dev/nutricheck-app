package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.BaseRecipeRepository
import com.frontend.nutricheck.client.ui.view_model.create_recipe.BaseCreateRecipeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class CreateRecipeViewModel(
    private val repository: BaseRecipeRepository
) : BaseCreateRecipeViewModel<Recipe>(
    initialDraft = Recipe()
) {
    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val ingredients = draft.map { it.ingredients }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val description = draft.map { it.description }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val energyInKcal = draft.map { it.calories }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)


    //TODO: Implement the logic to add ingredients, remove ingredients, and update the recipe draft.



    override fun validate(draft: Recipe): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun persistDraft(draft: Recipe): Result<Unit> {
        TODO("Not yet implemented")
    }

}