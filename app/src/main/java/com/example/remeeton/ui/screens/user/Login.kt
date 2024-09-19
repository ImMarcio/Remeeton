package com.example.remeeton.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.repository.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.let

val userDAO: UserDAO = UserDAO()

@Composable
fun TelaLogin(
    modifier: Modifier = Modifier,
    onSigninClick: (String) -> Unit,
    onRegisterClick: () -> Unit,
) {
    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }
    val scope = rememberCoroutineScope()

    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }

    fun login(email: String, password: String) {
        scope.launch(Dispatchers.IO) {
            userDAO.findByEmail(email) { usuario ->
                if (usuario != null && usuario.password == password) {
                    preferencesUtil.currentUserId = usuario.id
                    usuario.id?.let { onSigninClick(it) }
                } else {
                    messageError = "Login ou senha inválidos!"
                }
            }
        }
    }

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
            value = login,
            onValueChange = { login = it },
            label = { Text(text = "Login") },
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
                login(login, password)
            }
        ) {
            Text("Entrar", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(10.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onRegisterClick() }
        ) {
            Text("Cadastre-se", fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
        }

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }
}