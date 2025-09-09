package com.example.IwantToBelieve.core.util

sealed class ValidationResult  {
    object sucess : ValidationResult()
    data class Error(val errorMessage: String) : ValidationResult()
}