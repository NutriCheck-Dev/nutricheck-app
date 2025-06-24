package com.frontend.nutricheck.client.ui.view_model.recipe_list

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseRecipeListViewModel : BaseViewModel () {

    abstract fun onClickAddRecipe()

    abstract fun onRecipeClick()

    abstract fun onDetailsClick()

    abstract fun onMyRecipesClick()

    abstract fun onOnlieRecipesClick()
}