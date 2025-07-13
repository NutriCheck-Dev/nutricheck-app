package com.frontend.nutricheck.client.ui.view_model.recipe.overview

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseRecipeOverviewViewModel : BaseViewModel() {

    abstract fun onEditClicked()

    abstract suspend fun onDeleteRecipe(recipe: Recipe)
    abstract suspend fun onShareRecipe(recipe: Recipe)
    abstract fun addToMealClick(recipe: Recipe)

}