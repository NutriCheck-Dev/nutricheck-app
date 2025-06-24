package com.frontend.nutricheck.client.model.exceptions

sealed class ModelException(message: String?=null, cause:Throwable?=null): Exception(message, cause)
