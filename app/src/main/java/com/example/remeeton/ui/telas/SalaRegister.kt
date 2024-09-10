package com.example.remeeton.ui.telas

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remeeton.model.dados.PreferencesUtil
import com.example.remeeton.model.dados.Room
import com.example.remeeton.model.dados.RoomDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
val roomDao = RoomDao()

@Composable
fun SalaRegister(
    modifier: Modifier,
    onRegisterRoomClick: (String) -> Unit ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var reservada by remember { mutableStateOf(false) }
    var mensagemErro by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = nome,
            onValueChange = {nome = it},
            label = { Text(text = "Nome") })
        Spacer(modifier =  Modifier.height(10.dp))
        OutlinedTextField(
            value = descricao,
            onValueChange = {descricao = it},
            label = { Text(text = "descricao") })

        Spacer(modifier =  Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if(nome.isNotEmpty() && descricao.isNotEmpty() ) {
                    val sala = Room(nome = nome, descricao = descricao, reservada = reservada)
                    scope.launch(Dispatchers.IO) {
                        roomDao.adicionar(sala){ sucesso ->
                            if(sucesso) {
                                if (currentUserId != null) {
                                    onRegisterRoomClick(currentUserId)
                                }
                            } else{
                                mensagemErro = "Erro ao cadastrar Sala!"
                            }
                        }
                    }
                }else{
                    mensagemErro = "Preecha os campos do formulário!"
                }

            }) {
            Text("Cadastrar")
        }

        mensagemErro?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                mensagemErro = null
            }
        }
    }
}