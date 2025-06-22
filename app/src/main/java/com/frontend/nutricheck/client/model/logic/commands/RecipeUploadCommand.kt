package com.frontend.nutricheck.client.model.logic.commands

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.repositories.IRecipeRepository
import com.frontend.nutricheck.client.model.commands.ICommand

class RecipeUploadCommand(private val recipeRepository: IRecipeRepository, private val recipe: Recipe) : ICommand {
    //Sollte Rezept über service holen, dann über Api service hochladen
    override suspend fun execute() {
        recipeRepository.addRecipe(recipe)
    }

    override suspend fun undo() {
        recipeRepository.removeRecipe(recipe.id)
    }
}