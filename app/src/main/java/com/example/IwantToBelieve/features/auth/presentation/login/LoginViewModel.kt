package com.example.IwantToBelieve.features.auth.presentation.login

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Representa o estado da UI da tela de login
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccessful: Boolean = false,
    val errorMessage: String? = null
)

class LoginViewModel : ViewModel() {

    // Instância do Firebase Auth
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Campos de texto observáveis
    var email = mutableStateOf("")
    var password = mutableStateOf("")

    // StateFlow para o estado da UI
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun loginUser() {
        // Validação básica
        if (email.value.isBlank() || password.value.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Email e senha não podem estar vazios.") }
            return
        }

        // Inicia o processo de login
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                auth.signInWithEmailAndPassword(email.value.trim(), password.value).await()
                // Sucesso
                _uiState.update { it.copy(isLoading = false, isLoginSuccessful = true) }
            } catch (e: Exception) {
                // Falha
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Ocorreu um erro desconhecido."
                    )
                }
            }
        }
    }

    // Função para limpar a mensagem de erro depois de exibida
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}