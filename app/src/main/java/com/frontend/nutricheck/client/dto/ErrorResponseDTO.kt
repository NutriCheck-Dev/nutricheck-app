package com.frontend.nutricheck.client.dto

data class ErrorResponseDTO(
    val title: String,
    val status: Int,
    val detail: String
)