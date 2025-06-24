package com.frontend.nutricheck.client.model.logic.commands

sealed class CommandResult(open val message: String?) {
    data class Success(override val message: String? = null) : CommandResult(message)
    data class Failure(override val message: String) : CommandResult(message)
}