package com.frontend.nutricheck.client.model.repositories.ai

import com.frontend.nutricheck.client.model.logic.commands.CommandInvoker

interface BaseAiPictureRepository {
    val commandInvoker: CommandInvoker
    /**
     * Uploads a picture to the AI service for processing.
     *
     * @param imagePath The path to the image file to be uploaded.
     * @return A string representing the result of the upload operation.
     */
    suspend fun uploadPicture(imagePath: String): String

    /**
     * Retrieves the processed data from the AI service.
     *
     * @return A string containing the processed data.
     */
    suspend fun getProcessedData(): String
}