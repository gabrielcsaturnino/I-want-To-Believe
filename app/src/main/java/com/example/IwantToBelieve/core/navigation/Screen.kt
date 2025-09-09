package com.example.IwantToBelieve.core.navigation

sealed class Screen(val route: String) {
    object LoginScreen : Screen("login_screen")
    object HomeScreen : Screen("home_screen")
    object FeedScreen : Screen("feed_screen")
    object RegisterScreen : Screen("register_screen")
    object ProfileScreen: Screen("profile_screen")
    object AddPostScreen: Screen("add_post_screen")
}