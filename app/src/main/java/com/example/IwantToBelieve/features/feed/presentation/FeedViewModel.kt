package com.example.IwantToBelieve.features.feed.presentation

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.IwantToBelieve.features.post.domain.model.Post
import com.example.IwantToBelieve.features.post.presentation.PostUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class FeedViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _feed = MutableStateFlow<List<PostUI>>(emptyList())
    val feed: StateFlow<List<PostUI>> = _feed

    init {
        loadFeed()
    }



    private fun loadFeed() {
        viewModelScope.launch {
            try {

                val snapshot = db.collection("posts")
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .get()
                    .await()

                val posts = snapshot.toObjects(Post::class.java)

                _feed.value = posts.map { post ->
                    PostUI(
                        id = post.uid,
                        authorName = auth.currentUser?.displayName ?: "Unknown",
                        authorPhotoUrl = null,
                        description = post.description,
                        photoUrl = post.photoUrl,
                        likeCount = post.likes.size,
                        commentCount = post.comments.size,
                        isLikedByCurrentUser = post.likes.any { it.userId == auth.currentUser?.uid },
                        createdAtFormatted = post.createdAt.toDate().toString()
                    )

                }
            } catch (e: Exception) {
                e.printStackTrace()
                _feed.value = emptyList()
            }
        }
    }


    fun addPost(
        description: String,
        photoUrl: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        val currentUser = auth.currentUser ?: return onError()

        val post = hashMapOf(
            "uid" to db.collection("posts").document().id,
            "userId" to currentUser.uid,
            "photoUrl" to photoUrl,
            "description" to description,
            "likes" to emptyList<Map<String, Any>>(),
            "comments" to emptyList<Map<String, Any>>(),
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("posts")
            .add(post)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onError() }
    }

    fun toggleLike(post: PostUI) {
        val userId = auth.currentUser?.uid ?: return

        val updatedPosts = _feed.value.map {
            if (it.id == post.id) {
                val alreadyLiked = it.isLikedByCurrentUser
                it.copy(
                    isLikedByCurrentUser = !alreadyLiked,
                    likeCount = if (alreadyLiked) it.likeCount - 1 else it.likeCount + 1
                )
            } else it
        }
        _feed.value = updatedPosts

        val postRef = db.collection("posts").document(post.id)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(postRef)
            val likes = snapshot["likes"] as? List<Map<String, Any>> ?: emptyList()

            val alreadyLiked = likes.any { it["userId"] == userId }

            val newLikes = if (alreadyLiked) {
                likes.filterNot { it["userId"] == userId }
            } else {
                likes + mapOf("userId" to userId, "timestamp" to com.google.firebase.Timestamp.now())
            }

            transaction.update(postRef, "likes", newLikes)
        }.addOnFailureListener {
            _feed.value = _feed.value.map {
                if (it.id == post.id) post else it
            }
        }
    }
}
