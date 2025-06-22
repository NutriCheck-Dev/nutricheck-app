package com.frontend.nutricheck.client.model.commands

interface ICommand {
    /**
     * Executes the command.
     *
     * @throws Exception if the command fails to execute.
     */
    suspend fun execute()

    /**
     * Undoes the command.
     *
     * @throws Exception if the command fails to undo.
     */
    suspend fun undo()
}