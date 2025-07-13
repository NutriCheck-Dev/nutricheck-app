package com.frontend.nutricheck.client.ui.view_model.recipe.page

import com.frontend.nutricheck.client.model.data_sources.data.Recipe
import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseRecipePageViewModel : BaseViewModel () {

    abstract fun onMyRecipesClick()

    abstract fun onOnlineRecipesClick()
    abstract suspend fun onSaveRecipeClick(recipe: Recipe)
    abstract suspend fun onDeleteRecipeClick(recipe: Recipe)

}