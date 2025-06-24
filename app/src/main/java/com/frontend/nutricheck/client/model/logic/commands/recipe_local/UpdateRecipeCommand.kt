package com.frontend.nutricheck.client.model.logic.commands.recipe_local

import com.frontend.nutricheck.client.model.data_layer.Recipe
import com.frontend.nutricheck.client.model.logic.commands.Command
import com.frontend.nutricheck.client.model.logic.commands.CommandResult
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider

class UpdateRecipeCommand(
    private val databaseProvider: DatabaseProvider,
    private val recipe: Recipe
) : Command {

    override suspend fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override suspend fun undo(): CommandResult {
        TODO("Not yet implemented")
    }
}