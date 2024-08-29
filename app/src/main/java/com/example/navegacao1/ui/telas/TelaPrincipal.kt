package com.example.navegacao1.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.navegacao1.model.dados.Room
import com.example.navegacao1.model.dados.RoomDao
import com.example.navegacao1.model.dados.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun TelaPrincipal(modifier: Modifier = Modifier, onLogoffClick: () -> Unit) {
    val context = LocalContext.current
    var scope = rememberCoroutineScope()

    Column(modifier = modifier) {
        Text(text = "Tela Principal")
        val usuarios = remember { mutableStateListOf<Usuario>() }
        val salas = remember { mutableStateListOf<Room>() }
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
        Button(onClick = { onLogoffClick() }) {
            Text("Sair")
        }

        LazyColumn {
            items(usuarios) { usuario ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = usuario.nome)
                    }
                }
            }
            items(salas) { sala ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text(text = sala.nome)
                        Button(onClick = {
                            scope.launch(Dispatchers.IO) {
                                sala.id?.let { id ->
                                    roomDao.excluirSalaPorId(id) { sucesso ->
                                        if (sucesso) {

                                            // Ação adicional em caso de sucesso, se necessário
                                            println("Sala excluída com sucesso.")
                                        } else {
                                            // Tratamento de erro, se necessário
                                            println("Falha ao excluir a sala.")
                                        }
                                    }
                                }
                            }

                        }) {

                        }
                    }
                }
            }
        }
    }

}