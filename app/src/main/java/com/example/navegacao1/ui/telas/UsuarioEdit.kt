package com.example.navegacao1.ui.telas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.navegacao1.model.dados.Usuario
import com.example.navegacao1.model.dados.UsuarioDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun EditarUsuarioView(
    usuarioId: String,
    navController: NavController,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var usuario by remember { mutableStateOf<Usuario?>(null) }
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // Carregar dados do usuário
    LaunchedEffect(usuarioId) {
        scope.launch(Dispatchers.IO) {
            usuarioDAO.buscarUsuarioPorId(usuarioId) { usuarioRetornado ->
                if (usuarioRetornado != null) {
                    usuario = usuarioRetornado
                    nome = usuarioRetornado.nome
                    email = usuarioRetornado.email
                }
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        usuario?.let { u ->
            Text("Editar Usuário: ${u.nome}")
            // Campo para editar o nome
            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = nome,
                onValueChange = { nome = it },
                label = { Text("Nome") }
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )

            Row {
                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        usuario?.let { usuarioAtual ->
                            usuarioAtual.nome = nome
                            usuarioAtual.email = email
                            usuarioDAO.atualizarUsuario(usuarioAtual) { sucesso ->
                                if (sucesso) {
                                    println("Usuário atualizado com sucesso.")
                                    if (currentUserId != null) {
                                        onEditClick(currentUserId)
                                    }
                                } else {
                                    println("Falha ao atualizar o usuário.")
                                }
                            }
                        }
                    }
                }) {
                    Text("Salvar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    usuario?.id?.let { id ->
                        scope.launch(Dispatchers.IO) {
                            usuarioDAO.excluirUsuarioPorId(id) { sucesso ->
                                if (sucesso) {
                                    println("Usuário excluído com sucesso.")
                                    navController.navigate("principal")
                                } else {
                                    println("Falha ao excluir o usuário.")
                                }
                            }
                        }
                    }
                }) {
                    Text("Excluir")
                }
            }
        } ?: Text("Carregando...")
    }
}