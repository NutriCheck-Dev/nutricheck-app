package com.frontend.nutricheck.client.ui.view_model.recipe.page

import com.frontend.nutricheck.client.ui.view_model.BaseViewModel

abstract class BaseRecipePageViewModel : BaseViewModel () {

    abstract fun onClickAddRecipe()

    abstract fun onRecipeClick()

    abstract fun onDetailsClick()

    abstract fun onMyRecipesClick()

    abstract fun onOnlieRecipesClick()
}