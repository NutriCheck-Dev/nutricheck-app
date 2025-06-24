package com.frontend.nutricheck.client.model.logic.commands.settings


import com.frontend.nutricheck.client.model.logic.commands.Command
import com.frontend.nutricheck.client.model.logic.commands.CommandResult
import com.frontend.nutricheck.client.model.logic.commands.LanguageParams


class SetLanguageCommand(
    private val language: String
) : Command {

    override suspend fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override suspend fun undo(): CommandResult {
        TODO("Not yet implemented")
    }
}