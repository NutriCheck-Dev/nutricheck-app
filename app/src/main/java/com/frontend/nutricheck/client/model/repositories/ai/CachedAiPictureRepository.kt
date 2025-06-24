package com.frontend.nutricheck.client.model.repositories.ai

import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

class CachedAiPictureRepository: BaseAiPictureRepository {
    private val invoker = CommandInvoker()
    override val commandInvoker: CommandInvoker
        get() = invoker

    override suspend fun uploadPicture(imagePath: String): String {
        TODO("Not yet implemented")
    }

    override suspend fun getProcessedData(): String {
        TODO("Not yet implemented")
    }
}