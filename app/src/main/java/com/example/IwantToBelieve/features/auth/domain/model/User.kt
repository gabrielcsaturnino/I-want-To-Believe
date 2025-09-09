package com.example.IwantToBelieve.features.auth.domain.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

/**
 * Representa o perfil de um usuário armazenado no Cloud Firestore.
 *
 * @property uid O ID único do usuário, vindo do Firebase Authentication. É a chave primária.
 * @property name O nome completo do usuário, fornecido no cadastro.
 * @property email O e-mail do usuário, usado para login e contato.
 * @property createdAt A data e hora exatas em que a conta foi criada.
 */
@Keep // Garante que o Proguard/R8 não ofusque esta classe em builds de release.
data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String = "",
    val friendList: List<String> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)