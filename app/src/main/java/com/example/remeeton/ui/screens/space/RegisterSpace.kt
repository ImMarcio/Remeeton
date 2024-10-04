package com.example.remeeton.ui.screens.space

import Space
import SpaceDAO
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.remeeton.model.data.PreferencesUtil
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

val spaceDao = SpaceDAO()

@RequiresApi(Build.VERSION_CODES.O)
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
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var images by remember { mutableStateOf("") }
    val startTime by remember { mutableStateOf("") }
    val endTime by remember { mutableStateOf("") }

    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
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
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(text = "Descrição") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text(text = "Endereço") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text(text = "Latitude") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text(text = "Longitude") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text(text = "Capacidade") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = images,
            onValueChange = { images = it },
            label = { Text(text = "Imagens (URLs, separadas por vírgula)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (name.isNotEmpty() && description.isNotEmpty() &&
                    address.isNotEmpty() && latitude.isNotEmpty() && longitude.isNotEmpty() &&
                    capacity.isNotEmpty() && images.isNotEmpty()) {

                    val lat = latitude.toDoubleOrNull()
                    val long = longitude.toDoubleOrNull()
                    val cap = capacity.toIntOrNull()

                    val space = Space(
                        name = name,
                        description = description,
                        address = address,
                        latitude = lat ?: 0.0,
                        longitude = long ?: 0.0,
                        capacity = cap ?: 0,
                        startTime = startTime,
                        endTime = endTime,
                        images = images.split(",").map { it.trim() },
                        isReserved = false
                    )

                    scope.launch(Dispatchers.IO) {
                        spaceDao.add(space) { success ->
                            if (success) {
                                messageSuccess = "Espaço cadastrado com sucesso!"
                                currentUserId?.let { onRegisterRoomClick(it) }
                            } else {
                                messageError = "Erro ao cadastrar Espaço!"
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
