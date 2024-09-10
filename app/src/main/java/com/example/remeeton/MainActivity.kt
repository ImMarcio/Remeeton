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
import com.example.remeeton.ui.telas.EditarSalaView
import com.example.remeeton.ui.telas.EditarUsuarioView
import com.example.remeeton.ui.telas.SalaRegister
import com.example.remeeton.ui.telas.TelaLogin
import com.example.remeeton.ui.telas.TelaPrincipal
import com.example.remeeton.ui.telas.TelaRegister
import com.example.remeeton.ui.telas.roomDao
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
                                    navController.navigate("principal/$usuarioId")
                                },
                                onRegisterClick = {
                                    navController.navigate("cadastro")
                                },
                                onRegisterRoomClick = {
                                    navController.navigate("cadastro-sala")
                                }
                            )
                        }
                        composable("principal/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")
                            usuarioId?.let {
                                TelaPrincipal(
                                    navController = navController,
                                    usuarioId = it,
                                    modifier = Modifier.padding(innerPadding),
                                    onLogoffClick = {
                                        navController.navigate("login")
                                    }
                                )
                            }
                        }
                        composable("cadastro") {
                            TelaRegister(
                                modifier = Modifier.padding(innerPadding),
                                onRegisterClick = {
                                    navController.navigate("login")
                                })
                        }
                        composable("cadastro-sala") {
                            SalaRegister(modifier = Modifier.padding(innerPadding),
                                onRegisterRoomClick = { usuarioId ->
                                    navController.navigate("principal/$usuarioId")
                                })
                        }
                        composable("editar_sala/{salaId}") { backStackEntry ->
                            val salaId = backStackEntry.arguments?.getString("salaId")
                            salaId?.let {
                                EditarSalaView(
                                    roomDao,
                                    salaId = it,
                                    onEditClick = { usuarioId ->
                                        navController.navigate("principal/$usuarioId")
                                    })
                            }
                        }
                        composable("editar_usuario/{usuarioId}") { backStackEntry ->
                            val usuarioId = backStackEntry.arguments?.getString("usuarioId")
                            usuarioId?.let {
                                EditarUsuarioView(
                                    usuarioId = it,
                                    navController = navController,
                                    onEditClick = { usuarioId ->
                                        navController.navigate("principal/$usuarioId")
                                    })
                            }

                        }


                    }

                }
            }
        }
    }


    @Composable
    fun Greeting(name: String, modifier: Modifier = Modifier) {
//    TelaLogin()
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        Navegacao1Theme {
//       TelaLogin()
        }
    }
}