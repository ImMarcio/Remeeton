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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.remeeton.model.repository.firestore.SpaceDAO
import com.example.remeeton.ui.screens.Home
import com.example.remeeton.ui.screens.space.EditSpaceView
import com.example.remeeton.ui.screens.space.RegisterSpace
import com.example.remeeton.ui.screens.user.EditUserView
import com.example.remeeton.ui.screens.user.Login
import com.example.remeeton.ui.screens.user.RegisterUser
import com.example.remeeton.ui.theme.Navegacao1Theme
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        val spaceDAO = SpaceDAO()

        setContent {
            Navegacao1Theme {
                val navController = rememberNavController()
                val currentBackStackEntry by navController.currentBackStackEntryAsState()

                Scaffold(
                    topBar = {
                        ShowTopBar(navController = navController, currentBackStackEntry = currentBackStackEntry)
                    },
                    modifier = Modifier.fillMaxSize(),
                ) { innerPadding ->
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            Login(
                                modifier = Modifier.padding(innerPadding),
                                onSigninClick = { userId ->
                                    navController.navigate("home/$userId") {
                                        popUpTo("login") { inclusive = true } // Limpa a pilha de navegação
                                    }
                                },
                                onRegisterClick = {
                                    navController.navigate("register")
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
                                        navController.navigate("login") {
                                            popUpTo("home/$userId") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                        composable("register") {
                            RegisterUser(
                                modifier = Modifier.padding(innerPadding),
                                onRegisterClick = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                },
                                onReturnClick = {
                                    navController.navigate("login") {
                                        popUpTo("register") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("register-space") {
                            RegisterSpace(
                                modifier = Modifier.padding(innerPadding),
                                onRegisterRoomClick = { userId ->
                                    navController.navigate("home/$userId") {
                                        popUpTo("home/$userId") { inclusive = true }
                                    }
                                }
                            )
                        }
                        composable("edit-space/{spaceId}") { backStackEntry ->
                            val spaceId = backStackEntry.arguments?.getString("spaceId")
                            spaceId?.let {
                                EditSpaceView(
                                    spaceDAO = spaceDAO,
                                    spaceId = it,
                                    onEditClick = { userId ->
                                        navController.navigate("home/$userId") {
                                            popUpTo("home/$userId") { inclusive = true }
                                        }
                                    }
                                )
                            }
                        }
                        composable("edit-user/{userId}") { backStackEntry ->
                            val userId = backStackEntry.arguments?.getString("userId")
                            userId?.let {
                                EditUserView(
                                    navController = navController,
                                    onEditClick = { userId ->
                                        navController.navigate("home/$userId") {
                                            popUpTo("home/$userId") { inclusive = true } // Limpa a pilha de navegação
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowTopBar(navController: NavController, currentBackStackEntry: NavBackStackEntry?) {
    val route = currentBackStackEntry?.destination?.route
    if (route != "login" && route != "register") {
        TopAppBar(
            title = { Text("RemeetOn", color = MaterialTheme.colorScheme.primary) },
            modifier = Modifier.background(MaterialTheme.colorScheme.secondary),
        )
    }
}