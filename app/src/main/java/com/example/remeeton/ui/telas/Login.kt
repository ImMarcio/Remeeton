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
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


val userDAO: UserDAO = UserDAO()

@Composable
fun TelaLogin(
    modifier: Modifier = Modifier,
    onSigninClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onRegisterSpaceClick: () -> Unit) {

    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }

    val scope = rememberCoroutineScope()

    var login by remember {mutableStateOf("")}
    var password by remember {mutableStateOf("")}
    var messageError by remember { mutableStateOf<String?>(null) }

    fun login(email: String, password: String) {
        scope.launch(Dispatchers.IO) {
            userDAO.findByEmail(email) { usuario ->
                if (usuario != null && usuario.password == password) {
                    preferencesUtil.currentUserId = usuario.id
                    usuario.id?.let {onSigninClick(it) }
                } else {
                    messageError = "Login ou senha inválidos!"
                }
            }
        }
    }



    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = login,
            onValueChange = {login = it},
            label = { Text(text = "Login")})
        Spacer(modifier =  Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            onValueChange = {password = it},
            label = { Text(text = "Senha")})

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                login(login,password)
        }) {
            Text("Entrar")
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onRegisterClick()
            }) {
            Text("Cadastre-se")
        }

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }

}