package com.example.IwantToBelieve.features.auth.domain.model

import android.util.Patterns
import com.example.IwantToBelieve.core.util.ValidationResult

@JvmInline
value class Email private constructor(val value: String){
    companion object {
        fun create(value: String): ValidationResult {
            if (value.isEmpty()) {
                ValidationResult.Error("O email não pode estar vazio.")
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
                ValidationResult.Error("Email inválido.")
            }

            return ValidationResult.sucess
        }
    }
}