package com.frontend.nutricheck.client.dto
/**
 * Data transfer object representing an error response body.
 *
 * @property title The title or summary of the error.
 * @property status The HTTP status code associated with the error.
 * @property detail A detailed description of the error.
 */
data class ErrorResponseBodyDTO(
    val title: String,
    val status: Int,
    val detail: String
)