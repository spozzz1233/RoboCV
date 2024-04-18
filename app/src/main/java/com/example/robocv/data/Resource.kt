package com.example.robocv.data

import com.example.robocv.domain.ErrorType


sealed class Resource<T>(val data: T? = null, var error: ErrorType? = null, val message: String? = null) {
    class Success<T>(data: T): Resource<T>(data)
    class Error<T>(error: ErrorType?, message: String? = null, data: T? = null): Resource<T>(data, error, message)
}
