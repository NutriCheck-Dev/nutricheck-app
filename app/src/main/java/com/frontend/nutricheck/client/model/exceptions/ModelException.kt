package com.frontend.nutricheck.client.model.exceptions
/**
 * Base sealed class for all model-related exceptions in the application.
 *
 * @param message Optional error message describing the exception
 * @param cause Optional underlying cause that triggered this exception
 */
sealed class ModelException(message: String?=null, cause:Throwable?=null): Exception(message, cause)
