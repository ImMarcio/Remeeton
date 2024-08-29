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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.navegacao1.model.dados.Room
import com.example.navegacao1.model.dados.RoomDao
import com.example.navegacao1.model.dados.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaPrincipal(navController: NavController, modifier: Modifier = Modifier, onLogoffClick: () -> Unit) {
    val context = LocalContext.current
    var scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(text = "Tela Principal")
        val usuarios = remember { mutableStateListOf<Usuario>() }
        val salas = remember { mutableStateListOf<Room>() }
        Row {
            Button(onClick = {
                scope.launch(Dispatchers.IO) {
                    usuarioDAO.buscarUsuarios( callback = { usuariosRetornados ->
                        usuarios.clear()
                        usuarios.addAll(usuariosRetornados)
                    })
                    roomDao.buscarSalas ( callback = { salasRetornadas ->
                        salas.clear()
                        salas.addAll(salasRetornadas)
                    })


                }
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
                            Button(onClick = {
                                scope.launch(Dispatchers.IO) {
                                    usuario.id?.let { id ->
                                        usuarioDAO.excluirUsuarioPorId(id) { sucesso ->
                                            if (sucesso) {
                                                println("Usuário excluído com sucesso.")
                                            } else {
                                                println("Falha ao excluir o usuário.")
                                            }
                                        }
                                    }
                                }
                            }

                            )
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
//            items(salas) { sala ->
//                Card(modifier = Modifier.fillMaxWidth()) {
//                    Column {
//                        Text(text = sala.nome)
//                        Row {
//                            Button(onClick = {
//                                scope.launch(Dispatchers.IO) {
//                                    sala.id?.let { id ->
//                                        roomDao.excluirSalaPorId(id) { sucesso ->
//                                            if (sucesso) {
//                                                println("Sala excluída com sucesso.")
//                                            } else {
//                                                println("Falha ao excluir a sala.")
//                                            }
//                                        }
//                                    }
//                                }
//                            }) {
//                                Text(text = "Excluir")
//                            }
//
//                            Spacer(modifier = Modifier.width(8.dp))
//
//                            Button(onClick = {
//                                sala.id?.let { id ->
//                                    navController.navigate("editar_sala/$id")
//                                }
//                            }) {
//                                Text(text = "Editar")
//                            }
//
//                    }
//                }
//            }
//        }
    }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Salas", style = MaterialTheme.typography.titleMedium)

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
                        Spacer(modifier = Modifier.height(8.dp))
                        Row {
                            Button(
                                onClick = {
                                    scope.launch(Dispatchers.IO) {
                                        sala.id?.let { id ->
                                            roomDao.excluirSalaPorId(id) { sucesso ->
                                                if (sucesso) {
                                                    println("Sala excluída com sucesso.")
                                                } else {
                                                    println("Falha ao excluir a sala.")
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text(text = "Excluir")
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