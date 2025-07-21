package com.frontend.nutricheck.client.model.data_sources.data

sealed class Result<out T>  {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val code: Int? = null, val message: String?): Result<Nothing>()
}