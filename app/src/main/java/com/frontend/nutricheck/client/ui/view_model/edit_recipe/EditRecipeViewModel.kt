package com.frontend.nutricheck.client.ui.view_model

import androidx.lifecycle.viewModelScope
import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.recipe.BaseRecipeRepository
import com.frontend.nutricheck.client.ui.view_model.edit_recipe.BaseEditRecipeViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class EditRecipeViewModel(
    private val repository: BaseRecipeRepository
) : BaseEditRecipeViewModel<Recipe>(
    initialDraft = Recipe()
) {

    val title = draft.map { it.name }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val ingredients = draft.map { it.ingredients }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())
    val description = draft.map { it.description }.stateIn(viewModelScope, SharingStarted.Eagerly, "")
    val energyInKcal = draft.map { it.energyInKcal }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    override fun validate(draft: Recipe): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun persistDraft(draft: Recipe): Result<Unit> {
        TODO("Not yet implemented")
    }
    override fun onClickEditRecipe() {
        // Logic to edit the recipe draft
    }
    override fun onClickUploadRecipe() {
        // Logic to upload the edited recipe
    }
    override fun onDeleteRecipe() {
        // Logic to delete the recipe
    }
    override fun displayEditMenu() {
        // Logic to display the edit menu for the recipe
    }
    override fun addIngredient(ingredientId: String) {
        // Logic to add an ingredient to the recipe draft
    }
    override fun removeIngredient(ingredientId: String) {
        // Logic to remove an ingredient from the recipe draft
    }
    override fun onClickDiscardDraft() {
        // Logic to discard the recipe draft
    }
    override fun saveChanges() {
        // Logic to save changes made to the recipe draft
    }
}