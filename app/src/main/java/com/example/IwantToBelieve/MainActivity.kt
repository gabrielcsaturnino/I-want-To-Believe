package com.example.IwantToBelieve

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.IwantToBelieve.core.navigation.Screen
import com.example.IwantToBelieve.core.navigation.Screen.FeedScreen
import com.example.IwantToBelieve.core.navigation.Screen.RegisterScreen
import com.example.IwantToBelieve.features.auth.presentation.login.LoginScreen
import com.example.IwantToBelieve.features.auth.presentation.register.RegisterScreen
import com.example.IwantToBelieve.features.feed.presentation.FeedScreen
import com.example.IwantToBelieve.features.post.presentation.AddPostScreen
import com.example.IwantToBelieve.ui.theme.IwantToBelieveTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IwantToBelieveTheme {
                val navController = rememberNavController()
                val startDestination = if (VerifyAuthUser()) {
                    Screen.FeedScreen.route
                } else {
                    Screen.HomeScreen.route
                }
                NavHost(
                    navController = navController,
                    startDestination = startDestination
                ) {



                    composable(route = Screen.LoginScreen.route) {
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate(Screen.FeedScreen.route) {
                                            popUpTo(Screen.LoginScreen.route) {
                                                inclusive = true
                                            }
                                        }
                                    },
                                    onBackClick = {navController.navigate(Screen.HomeScreen.route) {popUpTo(0)}}
                                    )
                    }


                    composable(route = Screen.HomeScreen.route) {
                        HomeScreen(
                            onRegisterClick = { navController.navigate(Screen.RegisterScreen.route) },
                            onLoginClick = { navController.navigate(Screen.LoginScreen.route) }
                        )
                    }

                    composable(route = Screen.RegisterScreen.route) {
                        RegisterScreen(
                            onBackClick = { navController.navigate(Screen.HomeScreen.route) {popUpTo(0)} },
                            onRegisterSuccess = {navController.navigate(Screen.FeedScreen.route) {popUpTo(0)} }
                        )
                    }

                    composable(Screen.FeedScreen.route) {
                        FeedScreen(
                            onPostAdded = {
                                navController.navigate(Screen.AddPostScreen.route)
                            },
                            navController = navController
                        )
                    }
                    composable(Screen.AddPostScreen.route) {
                        AddPostScreen(
                            viewModel = viewModel(),
                            onPostAdded = {
                                navController.navigate(FeedScreen.route)
                            }
                        )
                    }


                }
                }
            }
        }
    }



@Composable
fun HomeScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit
) {

    Column (verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Seja bem-vindo ao IwantToBelieve!", modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.headlineMedium)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       Button(onClick = onRegisterClick, modifier = Modifier.padding(8.dp)) {
           Text(text = "Register")
       }
       Button(onClick = onLoginClick, modifier = Modifier.padding(8.dp)) {
           Text(text = "Login")
       }

    }
}


fun VerifyAuthUser(): Boolean {
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    if(currentUser != null) {
        return true
    }

    return false
}