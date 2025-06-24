package com.frontend.nutricheck.client.model.logic.commands

interface Command {

    /**
     * Executes the command.
     *
     * @throws Exception if the command fails to execute.
     */
    suspend fun execute(): CommandResult

    /**
     * Undoes the command.
     *
     * @throws Exception if the command fails to undo.
     */
    suspend fun undo(): CommandResult
}