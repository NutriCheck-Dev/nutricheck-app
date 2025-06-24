package com.frontend.nutricheck.client.model.logic.commands.user

import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.logic.commands.Command
import com.frontend.nutricheck.client.model.logic.commands.CommandResult
import com.frontend.nutricheck.client.model.logic.commands.UserDataParams

class SaveUserDataCommand(
    private val userData: UserData
) : Command {

    override suspend fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override suspend fun undo(): CommandResult {
        TODO("Not yet implemented")
    }
}