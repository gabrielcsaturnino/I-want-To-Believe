package com.example.IwantToBelieve.features.auth.domain.model

import com.example.IwantToBelieve.core.util.ValidationResult

sealed class Password private constructor(val value: String){
    companion object {
        fun create(value: String): ValidationResult {
            if (value.isEmpty()) {
                ValidationResult.Error("A senha não pode estar vazia.")
            }

            if (value.length < 8) {
                ValidationResult.Error("A senha deve ter no mínimo 8 caracteres.")
            }

            if (!value.any { it.isUpperCase() }) {
                ValidationResult.Error("A senha deve conter pelo menos uma letra maiúscula.")
            }

            return ValidationResult.sucess
        }
    }
}