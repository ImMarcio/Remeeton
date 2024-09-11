package com.example.remeeton.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.remeeton.model.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun RegisterUser(
    modifier: Modifier,
    onRegisterClick: () -> Unit ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = login,
            onValueChange = {login = it},
            label = { Text(text = "Nome") })
        Spacer(modifier =  Modifier.height(10.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = { Text(text = "Email") })

        Spacer(modifier =  Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {password = it},
            label = { Text(text = "Senha") })
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            if(login.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                val user = User(name = login, email = email, password = password)
                scope.launch(Dispatchers.IO) {
                    userDAO.add(user){ sucesso ->
                        if(sucesso) {
                            onRegisterClick()
                        } else{
                            messageError = "Erro ao cadastrar usuário!"
                        }
                    }
            }
            }else{
                messageError = "Preecha os campos do formulário!"
            }

        }) {
            Text("Cadastrar")
        }

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }
}