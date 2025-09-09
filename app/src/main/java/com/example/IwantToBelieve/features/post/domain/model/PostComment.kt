package com.example.IwantToBelieve.features.post.domain.model

import com.google.firebase.Timestamp

data class PostComment(
    val uid: String = "",
    val userId: String = "",
    val postId: String = "",
    val content: String = "",
    val createdAt: Timestamp = Timestamp.now()

)