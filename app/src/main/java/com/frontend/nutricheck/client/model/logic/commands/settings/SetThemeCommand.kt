package com.frontend.nutricheck.client.model.logic.commands.settings

import android.content.res.Resources.Theme
import com.frontend.nutricheck.client.model.logic.commands.Command
import com.frontend.nutricheck.client.model.logic.commands.CommandResult
import com.frontend.nutricheck.client.model.logic.commands.ThemeParams

class SetThemeCommand(
    private val theme: Theme
) : Command {

    override suspend fun execute(): CommandResult {
        TODO("Not yet implemented")
    }

    override suspend fun undo(): CommandResult {
        TODO("Not yet implemented")
    }
}