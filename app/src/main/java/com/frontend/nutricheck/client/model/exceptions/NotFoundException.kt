package com.frontend.nutricheck.client.model.exceptions
/**
 * Exception thrown when a requested resource cannot be found.
 *
 * @param responseMessage Description of the resource that was not found
 */
class NotFoundException(responseMessage: String):
    ModelException("Not found: $responseMessage")