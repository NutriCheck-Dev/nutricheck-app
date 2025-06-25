package com.frontend.nutricheck.client.model.repositories.user

import android.content.Context
import com.frontend.nutricheck.client.model.data_layer.UserData
import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker
import com.frontend.nutricheck.client.model.persistence.DatabaseProvider

class UserDataRepository(private val context: Context): BaseUserDataRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    private val userDataDao = DatabaseProvider.getDatabase(context).userDataDao()

    override suspend fun getUserData(): List<UserData> {
        TODO("Not yet implemented")
    }



}