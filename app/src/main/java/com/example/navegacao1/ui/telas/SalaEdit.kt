package com.example.navegacao1.ui.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.navegacao1.model.dados.Room
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.navegacao1.model.dados.PreferencesUtil
import com.example.navegacao1.model.dados.RoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun EditarSalaView(
    roomDao: RoomDao,
    salaId: String,
    onEditClick: (String) -> Unit) {
    val context = LocalContext.current

    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    val scope = rememberCoroutineScope()
    var sala by remember { mutableStateOf<Room?>(null) }
    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    // Buscar a sala pelo ID
    LaunchedEffect(salaId) {
        roomDao.buscarSalaPorId(salaId) { result ->
            sala = result
            if (result != null) {
                nome = result.nome
                descricao = result.descricao
            }
        }
    }

    // Tela de edição
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Editar Sala", style = MaterialTheme.typography.bodySmall)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da Sala") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição da Sala") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (sala != null) {
                scope.launch(Dispatchers.IO) {
                    val novosDados = mapOf(
                        "nome" to nome,
                        "descricao" to descricao
                    )
                    roomDao.editarSala(salaId, novosDados) { sucesso ->
                        if (sucesso) {
                            if (currentUserId != null) {
                                onEditClick(currentUserId)
                            }
                        } else {
                            // Exibir uma mensagem de erro
                        }
                    }
                }
            }
        }) {
            Text("Salvar")
        }
    }
}

