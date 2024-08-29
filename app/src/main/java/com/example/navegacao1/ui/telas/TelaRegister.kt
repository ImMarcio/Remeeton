package com.example.navegacao1.ui.telas

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
import com.example.navegacao1.model.dados.Usuario
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaRegister(
    modifier: Modifier,
    onRegisterClick: () -> Unit ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

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
            value = senha,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {senha = it},
            label = { Text(text = "Senha") })
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
            if(login.isNotEmpty() && email.isNotEmpty() && senha.isNotEmpty()) {
                val user = Usuario(nome = login, email = email, senha = senha)
                scope.launch(Dispatchers.IO) {
                    usuarioDAO.adicionar(user){ sucesso ->
                        if(sucesso) {
                            onRegisterClick()
                        } else{
                            mensagemErro = "Erro ao cadastrar usuário!"
                        }
                    }
            }
            }else{
                mensagemErro = "Preecha os campos do formulário!"
            }

        }) {
            Text("Cadastrar")
        }

        mensagemErro?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mensagemErro = null
            }
        }
    }
}