package com.example.navegacao1.ui.telas

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
import com.example.navegacao1.model.dados.PreferencesUtil
import com.example.navegacao1.model.dados.Room
import com.example.navegacao1.model.dados.RoomDao
import com.example.navegacao1.model.dados.Usuario
import com.example.navegacao1.model.dados.UsuarioDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaPrincipal(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLogoffClick: () -> Unit,
    usuarioId: String
) {
    val context = LocalContext.current
    var scope = rememberCoroutineScope()

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    var salas by remember { mutableStateOf<List<Room>>(emptyList()) }

    val usuarioDAO = UsuarioDAO()
    val roomDAO = RoomDao()

    var mensagemErro by remember { mutableStateOf<String?>(null) }
    var mensagemSucesso by remember { mutableStateOf<String?>(null) }
    // Função para carregar as salas e usuários
    fun carregarDados() {
        scope.launch(Dispatchers.IO) {
            usuarioDAO.buscarUsuarios { usuariosRetornados ->
                usuarios = usuariosRetornados
            }
            roomDAO.buscarSalas { salasRetornadas ->
                salas = salasRetornadas
            }
        }
    }

    // Função para excluir usuário
    fun excluirUsuario(id: String) {
        scope.launch(Dispatchers.IO) {
            usuarioDAO.excluirUsuarioPorId(id) { sucesso ->
                if (sucesso) {
                    mensagemSucesso = "Usuário excluído com sucesso."
                    carregarDados() // Recarregar dados após exclusão
                } else {
                    mensagemErro = "Falha ao excluir o usuário."
                }
            }
        }
    }

    // Função para excluir sala
    fun excluirSala(id: String) {
        scope.launch(Dispatchers.IO) {
            roomDAO.excluirSalaPorId(id) { sucesso ->
                if (sucesso) {
                    mensagemSucesso = "Sala excluída com sucesso."
                    carregarDados() // Recarregar dados após exclusão
                } else {
                    mensagemErro = "Falha ao excluir a sala."
                }
            }
        }
    }


    // Função para reservar sala
    fun reservarSala(salaId: String) {
        usuarioDAO.reservarSala(salaId, usuarioId) { sucesso ->
            if (sucesso) {
                mensagemSucesso = "Sala reservada com sucesso."
                carregarDados() // Recarregar dados após reserva
            } else {
                mensagemErro = "Falha ao reservar a sala."
            }
        }
    }
    // Função para desmarcar sala
    fun desmarcarSala(salaId: String) {
        usuarioDAO.cancelarReservaSala(usuarioId, salaId) { sucesso ->
            if (sucesso) {
                mensagemSucesso = "Reserva cancelada com sucesso."
                carregarDados() // Recarregar dados após cancelamento
            } else {
                mensagemErro = "Falha ao cancelar a reserva."
            }
        }
    }

    Column(modifier = modifier) {
        Text(text = "Tela Principal")
        mensagemSucesso?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }
        mensagemErro?.let { Text(text = it, style = MaterialTheme.typography.titleMedium) }

        Row {
            Button(onClick = {
                carregarDados()
            }) {
                Text("Carregar")
            }
            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = { onLogoffClick() }) {
                Text("Sair")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Usuários", style = MaterialTheme.typography.titleMedium)


        LazyColumn {
            items(usuarios) { usuario ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium) {
                    Column {
                        Text(text = usuario.nome, style = MaterialTheme.typography.titleMedium)
                        Text(text = usuario.email, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(onClick = { usuario.id?.let { excluirUsuario(it) }})
                            {
                                Text(text = "Excluir")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(onClick = {
                                usuario.id?.let { id ->
                                    navController.navigate("editar_usuario/$id")
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

//        Text(text = "Salas $usuarioId  user: $currentUserId", style = MaterialTheme.typography.titleMedium)
          Text(text = "Salas ", style = MaterialTheme.typography.titleMedium)

        LazyColumn {
            items(salas) { sala ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = sala.nome, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (sala.reservadoPor == null) "Disponível" else "Reservada",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = { sala.id?.let { excluirSala(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Excluir")
                            }
                            Spacer(modifier = Modifier.width(8.dp))


                            Button(
                                onClick = { sala.id?.let { reservarSala(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Reservar")
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { sala.id?.let { desmarcarSala(it) } },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Unreserva")
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = {
                                    sala.id?.let { id ->
                                        navController.navigate("editar_sala/$id")
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