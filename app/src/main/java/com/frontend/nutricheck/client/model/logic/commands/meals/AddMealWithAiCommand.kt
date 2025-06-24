package com.frontend.nutricheck.client.model.logic.commands.meals

import com.frontend.nutricheck.client.model.data_layer.Meal
import com.frontend.nutricheck.client.model.logic.commands.Command
import com.frontend.nutricheck.client.model.logic.commands.CommandResult
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider

class AddMealWithAiCommand(
    private val databaseProvider: DatabaseProvider,
    private val meal: Meal
) : Command {

    override suspend fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override suspend fun undo(): CommandResult {
        TODO("Not yet implemented")
    }

}