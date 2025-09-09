package com.example.IwantToBelieve.features.user.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.IwantToBelieve.features.auth.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class UserUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val selectedUser: User? = null,
    val error: String? = null
)

class UserViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> = _uiState

    /**
     * Pesquisar usuários pelo nome (case-insensitive).
     */
    fun searchUsersByName(query: String) {
        if (query.isBlank()) {
            _uiState.value = _uiState.value.copy(users = emptyList())
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val snapshot = db.collection("users")
                    .whereGreaterThanOrEqualTo("name", query)
                    .whereLessThanOrEqualTo("name", query + "\uf8ff")
                    .get()
                    .await()

                val users = snapshot.toObjects(User::class.java)
                _uiState.value = _uiState.value.copy(isLoading = false, users = users)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    /**
     * Selecionar usuário (exibir perfil).
     */
    fun selectUser(user: User) {
        _uiState.value = _uiState.value.copy(selectedUser = user)
    }

    /**
     * Adicionar amigo (atualiza friendList do usuário logado).
     */
    fun addFriend(friendId: String) {
        val currentUserId = auth.currentUser?.uid ?: return
        val userRef = db.collection("users").document(currentUserId)

        viewModelScope.launch {
            try {
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(userRef)
                    val friends = snapshot["friendList"] as? List<String> ?: emptyList()
                    if (!friends.contains(friendId)) {
                        transaction.update(userRef, "friendList", friends + friendId)
                    }
                }.await()

                // Atualiza o estado para refletir a adição
                val updatedFriends = _uiState.value.selectedUser?.friendList?.toMutableList() ?: mutableListOf()
                if (!updatedFriends.contains(friendId)) {
                    updatedFriends.add(friendId)
                }

                _uiState.value = _uiState.value.copy(
                    selectedUser = _uiState.value.selectedUser?.copy(friendList = updatedFriends)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
