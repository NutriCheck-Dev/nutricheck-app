package com.frontend.nutricheck.client.model.exceptions

class NotFoundException(responseMessage: String):
    ModelException("Not found: $responseMessage")