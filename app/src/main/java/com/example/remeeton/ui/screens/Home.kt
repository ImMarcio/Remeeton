package com.example.remeeton

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.Space
import com.example.remeeton.model.repository.SpaceDAO
import com.example.remeeton.model.repository.UserDAO
import com.example.remeeton.ui.components.MessageHandler
import com.example.remeeton.ui.components.SearchBar
import com.example.remeeton.ui.components.SpaceCard
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
    val userDAO = UserDAO()

    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }

    fun loadSpaces() {
        scope.launch(Dispatchers.IO) {
            spaceDAO.findAll { returnedSpaces -> spaces = returnedSpaces }
        }
    }

    fun bookSpace(spaceId: String) {
        userDAO.bookSpace(spaceId, userId) { success ->
            if (success) {
                messageSuccess = "Espaço reservado com sucesso."
                loadSpaces()
            } else {
                messageError = "Falha ao reservar o espaço."
            }
        }
    }

    fun cancelBookingSpace(spaceId: String) {
        userDAO.cancelBookingSpace(userId, spaceId) { success ->
            if (success) {
                messageSuccess = "Reserva cancelada com sucesso."
                loadSpaces()
            } else {
                messageError = "Falha ao cancelar a reserva."
            }
        }
    }

    LaunchedEffect(Unit) {
        loadSpaces()
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

    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )

        Button(
            onClick = { navController.navigate("register-space") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar Espaço")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar")
        }

        MessageHandler(
            messageSuccess = messageSuccess,
            messageError = messageError
        )

        LazyColumn {
            items(spaces.filter { it.name.contains(searchQuery, ignoreCase = true) }) { space ->
                SpaceCard(
                    space = space,
                    onBookSpace = { bookSpace(space.id!!) },
                    onCancelSpace = { cancelBookingSpace(space.id!!) },
                    onEditSpace = { navController.navigate("edit-space/${space.id}") }
                )
            }
        }
    }
}