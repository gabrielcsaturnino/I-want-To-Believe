package com.example.IwantToBelieve.features.feed.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.IwantToBelieve.core.navigation.Screen
import com.example.IwantToBelieve.features.post.presentation.PostUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    viewModel: FeedViewModel = viewModel(),
    onPostAdded: () -> Unit,
    navController: NavController
) {
    val posts by viewModel.feed.collectAsState(initial = emptyList())


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Feed") }
            )

            Button(onClick = {navController.navigate(Screen.ProfileScreen.route)}) {

            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.AddPostScreen.route)
            }) {
                Text("+")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(posts) { post ->
                PostItem(post, onLikeClick = { viewModel.toggleLike(it) })
            }
        }
    }
}



@Composable
fun PostItem(
    post: PostUI,
    onLikeClick: (PostUI) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = post.authorName, style = MaterialTheme.typography.titleMedium)
        Text(text = post.description, style = MaterialTheme.typography.bodyMedium)

        if (post.photoUrl.isNotBlank()) {
            Image(
                painter = rememberAsyncImagePainter(post.photoUrl),
                contentDescription = "Imagem do post",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                IconButton(onClick = { onLikeClick(post) }) {
                    if (post.isLikedByCurrentUser) {
                        Icon(
                            imageVector = Icons.Filled.Favorite,
                            contentDescription = "Descurtir",
                            tint = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Curtir"
                        )
                    }
                }
                Text("${post.likeCount}")
            }

            Text("${post.commentCount} comentários", style = MaterialTheme.typography.bodySmall)
        }
    }
}

