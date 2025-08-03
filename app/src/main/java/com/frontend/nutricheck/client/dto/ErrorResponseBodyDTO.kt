package com.frontend.nutricheck.client.dto

data class ErrorResponseBodyDTO(
    val title: String,
    val status: Int,
    val detail: String
)