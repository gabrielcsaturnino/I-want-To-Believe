package com.example.IwantToBelieve.features.post.presentation

data class PostUI(
    val id: String,
    val authorName: String,
    val authorPhotoUrl: String?,
    val description: String,
    val photoUrl: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLikedByCurrentUser: Boolean,
    val createdAtFormatted: String
)


