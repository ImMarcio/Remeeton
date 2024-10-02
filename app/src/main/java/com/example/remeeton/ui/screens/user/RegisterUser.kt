package com.example.remeeton.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remeeton.R
import com.example.remeeton.model.data.firestore.User
import com.example.remeeton.model.repository.firestore.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterUser(
    modifier: Modifier = Modifier,
    onRegisterClick: () -> Unit,
    onReturnClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userDAO = remember { UserDAO() }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val logo: Painter = painterResource(id = R.drawable.logo_remeeton)
        Image(
            painter = logo,
            contentDescription = "App Logo",
            modifier = Modifier
                .size(150.dp)
                .padding(16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Nome") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = { password = it },
            label = { Text(text = "Senha") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            onClick = {
                if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                    val user = User(
                        name = name,
                        email = email,
                        password = password,
                    )

                    scope.launch(Dispatchers.IO) {
                        userDAO.add(user) { success ->
                            if (success) {
                                userDAO.findByEmail(email) { registeredUser ->
                                    registeredUser?.let {
                                        onRegisterClick()
                                    } ?: run {
                                        messageError = "Erro ao recuperar usuário cadastrado!"
                                    }
                                }
                            } else {
                                messageError = "Erro ao cadastrar usuário!"
                            }
                        }
                    }
                } else {
                    messageError = "Preencha todos os campos do formulário!"
                }
            }
        ) {
            Text("Cadastrar", fontSize = 18.sp, color = Color.White)
        }

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onReturnClick() }
        ) {
            Text("Voltar", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }

        Spacer(modifier = Modifier.height(10.dp))

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }
}