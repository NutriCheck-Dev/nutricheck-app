package com.frontend.nutricheck.client.model.exceptions



class ServerErrorException(responseMessage: String):
    ModelException("Internal server error: $responseMessage")