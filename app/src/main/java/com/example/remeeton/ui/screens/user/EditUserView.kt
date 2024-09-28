package com.example.remeeton.ui.screens.user

import android.R.attr.label
import android.R.attr.text
import android.provider.SyncStateContract.Helpers.update
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.firestore.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.let

@Composable
fun EditUserView(
    navController: NavController,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId: String? = preferencesUtil.currentUserId

    var user by remember { mutableStateOf<User?>(null) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserId) {
        if (currentUserId != null) {
            scope.launch(Dispatchers.IO) {
                userDAO.findById(currentUserId) { userReturned ->
                    if (userReturned != null) {
                        user = userReturned
                        nome = userReturned.name
                        email = userReturned.email
                    }
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
        Text(text = "Editar Usuário", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (nome.isBlank() || email.isBlank()) {
                    errorMessage = "Todos os campos devem ser preenchidos"
                }
                if (user != null) {
                    scope.launch(Dispatchers.IO) {
                        user?.let { user ->
                            user.name = nome
                            user.email = email
                            userDAO.update(user) { success ->
                                if (success) {
                                    onEditClick(currentUserId.toString())
                                } else {
                                    errorMessage = "Falha ao salvar o usuário."
                                }
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                user?.id?.let { id ->
                    scope.launch(Dispatchers.IO) {
                        userDAO.deleteById(id) { success ->
                            if (success) {
                                println("Usuário excluído com sucesso.")
                                navController.navigate("home")
                            } else {
                                println("Falha ao excluir o usuário.")
                            }
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Excluir")
        }
    }
}