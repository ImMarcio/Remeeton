package com.example.remeeton.ui.screens.user

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.let

@Composable
fun EditUserView(
    userId: String,
    navController: NavController,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var user by remember { mutableStateOf<User?>(null) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        scope.launch(Dispatchers.IO) {
            userDAO.findById(userId) { userReturned ->
                if (userReturned != null) {
                    user = userReturned
                    nome = userReturned.name
                    email = userReturned.email
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar Usuário", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (user != null) {
                scope.launch(Dispatchers.IO) {
                    user?.let { usuarioAtual ->
                        usuarioAtual.name = nome
                        usuarioAtual.email = email
                        userDAO.update(usuarioAtual) { success ->
                            if (success) {
                                if (currentUserId != null) {
                                    onEditClick(currentUserId)
                                }
                            } else {
                            }
                        }
                    }
                }
            }
        }) {
            Text("Salvar")
        }
        Spacer(modifier = Modifier.width(8.dp))

        Button(onClick = {
            user?.id?.let { id ->
                scope.launch(Dispatchers.IO) {
                    userDAO.deleteById(id) { sucesso ->
                        if (sucesso) {
                            println("Usuário excluído com sucesso.")
                            navController.navigate("home")
                        } else {
                            println("Falha ao excluir o usuário.")
                        }
                    }
                }
            }
        }) {
            Text("Excluir")
        }
    }
}