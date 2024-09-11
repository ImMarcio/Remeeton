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
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.Space
import com.example.remeeton.model.data.SpaceDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
val spaceDao = SpaceDAO()

@Composable
fun RegisterSpace (
    modifier: Modifier,
    onRegisterRoomClick: (String) -> Unit ) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var reserved by remember { mutableStateOf(false) }
    var messageError by remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()) {

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = { Text(text = "Nome") })
        Spacer(modifier =  Modifier.height(10.dp))
        OutlinedTextField(
            value = description,
            onValueChange = {description = it},
            label = { Text(text = "description") })

        Spacer(modifier =  Modifier.height(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if(name.isNotEmpty() && description.isNotEmpty() ) {
                    val sala = Space(name = name, description = description, reserved = reserved)
                    scope.launch(Dispatchers.IO) {
                        spaceDao.add(sala){ success ->
                            if(success) {
                                if (currentUserId != null) {
                                    onRegisterRoomClick(currentUserId)
                                }
                            } else{
                                messageError = "Erro ao cadastrar Sala!"
                            }
                        }
                    }
                }else{
                    messageError = "Preecha os campos do formulário!"
                }

            }) {
            Text("Cadastrar")
        }

        messageError?.let {
            LaunchedEffect(it) {
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                messageError = null
            }
        }
    }
}