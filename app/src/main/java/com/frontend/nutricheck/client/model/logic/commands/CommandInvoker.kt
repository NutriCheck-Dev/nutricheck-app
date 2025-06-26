package com.frontend.nutricheck.client.model.logic.commands

class CommandInvoker {
    private val commands = mutableListOf<Command>()

     suspend fun executeCommand(command: Command) {
        commands.add(command)
        command.execute()
    }

    suspend fun undoCommand() {
        if (commands.isNotEmpty()) {
            commands.removeAt(commands.lastIndex).undo()
        }
    }


}