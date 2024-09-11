package com.example.remeeton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remeeton.ui.telas.EditSpaceView
import com.example.remeeton.ui.telas.EditUserView
import com.example.remeeton.ui.telas.Home
import com.example.remeeton.ui.telas.RegisterSpace
import com.example.remeeton.ui.telas.RegisterUser
import com.example.remeeton.ui.telas.TelaLogin
import com.example.remeeton.ui.telas.spaceDao
import com.example.remeeton.ui.theme.Navegacao1Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        setContent {
            Navegacao1Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("RemeetOn") },
                            Modifier.background(MaterialTheme.colorScheme.secondary)
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            TelaLogin(
                                modifier = Modifier.padding(innerPadding),
                                onSigninClick = { usuarioId ->
                                    navController.navigate("home/$usuarioId")
                                },
                                onRegisterClick = {
                                    navController.navigate("register")
                                },
                                onRegisterSpaceClick = {
                                    navController.navigate("register-space")
                                }
                            )
                        }
                        composable("home/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            userId?.let {
                                Home(
                                    navController = navController,
                                    userId = it,
                                    modifier = Modifier.padding(innerPadding),
                                    onLogoffClick = {
                                        navController.navigate("login")
                                    }
                                )
                            }
                        }
                        composable("register") {
                            RegisterUser(
                                modifier = Modifier.padding(innerPadding),
                                onRegisterClick = {
                                    navController.navigate("login")
                                })
                        }
                        composable("register-space") {
                            RegisterSpace(modifier = Modifier.padding(innerPadding),
                                onRegisterRoomClick = { userId ->
                                    navController.navigate("home/$userId")
                                })
                        }
                        composable("edit-space/{spaceId}") { backStackEntry ->
                            val spaceId = backStackEntry.arguments?.getString("spaceId")
                            spaceId?.let {
                                EditSpaceView(
                                    spaceDao,
                                    spaceId = it,
                                    onEditClick = { userId ->
                                        navController.navigate("home/$userId")
                                    })
                            }
                        }
                        composable("edit-user/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            userId?.let {
                                EditUserView(
                                    userId = it,
                                    navController = navController,
                                    onEditClick = { userId ->
                                        navController.navigate("home/$userId")
                                    })
                            }
                        }
                    }
                }
            }
        }
    }
}