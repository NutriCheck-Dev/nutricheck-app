package com.frontend.nutricheck.client.model.repositories.user

import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

interface BaseUserDataRepository {
    val commandInvoker: CommandInvoker
    suspend fun getUserData() : List<UserData>
}