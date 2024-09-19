package com.example.remeeton

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.remeeton.R
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.Space
import com.example.remeeton.model.repository.SpaceDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Home(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLogoffClick: () -> Unit,
    userId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }

    val spaceDAO = SpaceDAO()
    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }

    fun loadSpaces() {
        scope.launch(Dispatchers.IO) {
            spaceDAO.findAll { returnedSpaces ->
                spaces = returnedSpaces
            }
        }
    }

    fun bookSpace(spaceId: String) {
        spaceDAO.book(spaceId, userId) { success ->
            if (success) {
                messageSuccess = "Espaço reservado com sucesso."
                loadSpaces()
            } else {
                messageError = "Falha ao reservar o espaço."
            }
        }
    }

    fun cancelBookingSpace(spaceId: String) {
        spaceDAO.cancelBooking(spaceId) { success ->
            if (success) {
                messageSuccess = "Reserva cancelada com sucesso."
                loadSpaces()
            } else {
                messageError = "Falha ao cancelar a reserva."
            }
        }
    }

    LaunchedEffect(messageSuccess, messageError) {
        messageSuccess?.let {
            delay(3000)
            messageSuccess = null
        }
        messageError?.let {
            delay(3000)
            messageError = null
        }
    }

    LaunchedEffect(Unit) {
        loadSpaces()
    }

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                label = { Text("Buscar Espaços") },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Buscar")
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.navigate("register-space") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
                modifier = Modifier.height(56.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Adicionar Espaço")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Adicionar")
            }
        }

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

        LazyColumn {
            items(spaces.filter { it.name.contains(searchQuery, ignoreCase = true) }) { space ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = space.name,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        val statusText = if (space.reservedBy == null) "Disponível" else "Reservado"
                        val statusColor = if (space.reservedBy == null) Color.Blue else Color.Red
                        Text(
                            text = statusText,
                            color = statusColor,
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            if (space.reservedBy == null) {
                                Button(
                                    onClick = { space.id?.let { bookSpace(it) } },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                                ) {
                                    Text("Reservar", color = Color.White)
                                }
                            } else {
                                Button(
                                    onClick = { space.id?.let { cancelBookingSpace(it) } },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                ) {
                                    Text("Cancelar Reserva", color = Color.White)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    space.id?.let { id ->
                                        navController.navigate("edit-space/$id")
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Editar")
                            }
                        }
                    }
                }
            }
        }
    }
}