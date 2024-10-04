package com.example.remeeton.ui.screens.space

import Space
import SpaceDAO
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remeeton.model.data.PreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSpaceView(
    spaceDAO: SpaceDAO,
    spaceId: String,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId
    val scope = rememberCoroutineScope()
    var space by remember { mutableStateOf<Space?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var capacity by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var images by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(spaceId) {
        spaceDAO.findById(spaceId) { result ->
            space = result
            if (result != null) {
                name = result.name
                description = result.description
                address = result.address
                latitude = result.latitude.toString()
                longitude = result.longitude.toString()
                capacity = result.capacity.toString()
                startTime = result.startTime // Convertendo Timestamp para String
                endTime = result.endTime
                images = result.images.joinToString(", ")
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
        Text(
            text = "Editar Espaço",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Espaço") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Espaço") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Endereço") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = latitude,
            onValueChange = { latitude = it },
            label = { Text("Latitude") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = longitude,
            onValueChange = { longitude = it },
            label = { Text("Longitude") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = capacity,
            onValueChange = { capacity = it },
            label = { Text("Capacidade") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = startTime,
            onValueChange = { startTime = it },
            label = { Text("Horário de Início (HH:mm)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = endTime,
            onValueChange = { endTime = it },
            label = { Text("Horário de Término (HH:mm)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = images,
            onValueChange = { images = it },
            label = { Text("Imagens (URLs, separadas por vírgula)") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            onClick = {
                if (name.isBlank() || description.isBlank() || address.isBlank() ||
                    latitude.isBlank() || longitude.isBlank() || capacity.isBlank() ||
                    startTime.isBlank() || endTime.isBlank() || images.isBlank()) {
                    errorMessage = "Todos os campos devem ser preenchidos"
                } else {
                    space?.let {
                        scope.launch(Dispatchers.IO) {
                            val newData = mapOf(
                                "name" to name,
                                "description" to description,
                                "address" to address,
                                "latitude" to latitude.toDouble(),
                                "longitude" to longitude.toDouble(),
                                "capacity" to capacity.toInt(),
                                "startTime" to startTime, // Placeholder
                                "endTime" to endTime, // Placeholder
                                "images" to images.split(",").map { it.trim() }
                            )
                            spaceDAO.edit(spaceId, newData) { success ->
                                if (success) {
                                    if (currentUserId != null) {
                                        onEditClick(currentUserId)
                                    }
                                } else {
                                    errorMessage = "Falha ao editar o espaço."
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
    }
}
