package com.example.IwantToBelieve.features.auth.presentation.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.IwantToBelieve.core.util.ValidationResult
import com.example.IwantToBelieve.features.auth.domain.model.Email
import com.example.IwantToBelieve.features.auth.domain.model.Password
import com.example.IwantToBelieve.features.auth.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Estado para os erros de validação dos campos
data class ValidationState(
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

// Estado geral da UI
data class RegisterUiState(
    val isLoading: Boolean = false,
    val isRegisterSuccessful: Boolean = false,
    val registrationError: String? = null
)

class RegisterViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    private val _validationState = MutableStateFlow(ValidationState())
    val validationState = _validationState.asStateFlow()

    fun onNameChange(newName: String) { _name.value = newName }
    fun onEmailChange(newEmail: String) { _email.value = newEmail }
    fun onPasswordChange(newPassword: String) { _password.value = newPassword }

    fun clearRegistrationError() {
        _uiState.update { it.copy(registrationError = null) }
    }

    fun onRegisterClick() {
        val nameValue = _name.value.trim()
        val emailValue = _email.value.trim()
        val passwordValue = _password.value

        val isNameValid = nameValue.isNotBlank()
        val emailResult = Email.create(emailValue)
        val passwordResult = Password.create(passwordValue)

            val hasError = !isNameValid || emailResult is ValidationResult.Error || passwordResult is ValidationResult.Error

        if (hasError) {
            _validationState.update {
                it.copy(
                    nameError = if (!isNameValid) "O nome não pode estar vazio." else null,
                    emailError = (emailResult as? ValidationResult.Error)?.errorMessage,
                    passwordError = (passwordResult as? ValidationResult.Error)?.errorMessage
                )
            }
            return
        }

        viewModelScope.launch {
            _validationState.update { ValidationState() }
            _uiState.update { it.copy(isLoading = true) }

            try {
                val authResult = auth.createUserWithEmailAndPassword(emailValue, passwordValue).await()
                val uid = authResult.user?.uid

                if (uid != null) {
                    val newUser = User(uid = uid, name = nameValue, email = emailValue)
                    db.collection("users").document(uid).set(newUser).await()
                } else {
                    throw IllegalStateException("UID do usuário não encontrado após o registro.")
                }

                _uiState.update { it.copy(isLoading = false, isRegisterSuccessful = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRegisterSuccessful = false,
                        registrationError = e.message ?: "Ocorreu um erro desconhecido."
                    )
                }
            }
        }
    }
}