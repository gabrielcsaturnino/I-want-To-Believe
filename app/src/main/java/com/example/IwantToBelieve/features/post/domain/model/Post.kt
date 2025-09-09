package com.example.IwantToBelieve.features.post.domain.model

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Post (
    val uid: String = "",
    val userId: String = "",
    val photoUrl: String = "",
    val comments: List<PostComment> = emptyList(),
    val description: String = "",
    val likes: List<PostLike> = emptyList(),
    val createdAt: Timestamp = Timestamp.now()
)