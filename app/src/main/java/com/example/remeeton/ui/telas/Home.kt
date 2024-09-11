package com.example.remeeton.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.Space
import com.example.remeeton.model.data.SpaceDAO
import com.example.remeeton.model.data.User
import com.example.remeeton.model.data.UserDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun Home(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLogoffClick: () -> Unit,
    userId: String
) {
    val context = LocalContext.current
    var scope = rememberCoroutineScope()

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }

    val userDAO = UserDAO()
    val spaceDAO = SpaceDAO()

    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }
    fun loadData() {
        scope.launch(Dispatchers.IO) {
            userDAO.findAll { returnedUsers ->
                users = returnedUsers
            }
            spaceDAO.findAll { returnedSpaces ->
                spaces = returnedSpaces
            }
        }
    }

    fun deleteUserById(id: String) {
        scope.launch(Dispatchers.IO) {
            userDAO.deleteById(id) { success ->
                if (success) {
                    messageSuccess = "Usuário excluído com sucesso."
                    loadData()
                } else {
                    messageError = "Falha ao excluir o usuário."
                }
            }
        }
    }

    fun deleteSpaceById(id: String) {
        scope.launch(Dispatchers.IO) {
            spaceDAO.deleteById(id) { success ->
                if (success) {
                    messageSuccess = "Espaço excluído com sucesso."
                    loadData()
                } else {
                    messageError = "Falha ao excluir a sala."
                }
            }
        }
    }

    fun bookSpace(spaceId: String) {
        userDAO.bookSpace(spaceId, userId) { success ->
            if (success) {
                messageSuccess = "Espaço reservado com sucesso."
                loadData()
            } else {
                messageError = "Falha ao reservar o espaço."
            }
        }
    }

    fun cancelBookingSpace(spaceId: String) {
        userDAO.cancelBookingSpace(userId, spaceId) { success ->
            if (success) {
                messageSuccess = "Reserva cancelada com sucesso."
                loadData()
            } else {
                messageError = "Falha ao cancelar a reserva."
            }
        }
    }

    Column(modifier = modifier) {
        Text(text = "Tela Principal")
        messageSuccess?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
        messageError?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }

        Row {
            Button(onClick = {
                loadData()
            }) {
                Text("Carregar")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { onLogoffClick() }) {
                Text("Sair")
            }
            Button(onClick = {
                navController.navigate("register-space")
            }) {
                Text("Cadastrar Sala")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Usuários", style = MaterialTheme.typography.titleMedium)


        LazyColumn {
            items(users) { user ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium) {
                    Column {
                        Text(text = user.name, style = MaterialTheme.typography.titleMedium)
                        Text(text = user.email, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { user.id?.let { deleteUserById(it) }})
                            {
                                Text(text = "Excluir")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {
                                user.id?.let { id ->
                                    navController.navigate("edit-user/$id")
                                }
                            }) {
                                Text(text = "Editar")
                            }
                        }
                    }
                }
            }

    }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Espaços ", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(spaces) { space ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = space.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (space.reservedBy == null) "Disponível" else "Reservada",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { space.id?.let { deleteSpaceById(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Excluir")
                            }
                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = { space.id?.let { bookSpace(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Reservar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { space.id?.let { cancelBookingSpace(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Disponibizar")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    space.id?.let { id ->
                                        navController.navigate("edit-space/$id")
                                    }
                                }
                            ) {
                                Text(text = "Editar")
                            }
                        }
                    }
                }
            }
        }
}

}