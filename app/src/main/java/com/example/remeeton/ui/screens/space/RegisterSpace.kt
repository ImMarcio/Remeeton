package com.example.remeeton.ui.screens.space

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.Space
import com.example.remeeton.model.repository.SpaceDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val spaceDao = SpaceDAO()

@Composable
fun RegisterSpace(
    modifier: Modifier = Modifier,
    onRegisterRoomClick: (String) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reserved by remember { mutableStateOf(false) }
    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = "Cadastrar novo espaço",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text(text = "Nome") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Descrição") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && description.isNotEmpty()) {
                    val sala = Space(name = name, description = description, reserved = reserved)
                    scope.launch(Dispatchers.IO) {
                        spaceDao.add(sala) { success ->
                            if (success) {
                                messageSuccess = "Sala cadastrada com sucesso!"
                                currentUserId?.let { onRegisterRoomClick(it) }
                            } else {
                                messageError = "Erro ao cadastrar Sala!"
                            }
                        }
                    }
                } else {
                    messageError = "Preencha todos os campos!"
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Cadastrar", fontSize = 18.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        messageSuccess?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageSuccess = null
            }
        }

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }
}
