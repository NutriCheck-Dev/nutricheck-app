package com.frontend.nutricheck.client.model.exceptions

class TimeOutException(cause: Throwable): ModelException("HTTP request timed out", cause)
