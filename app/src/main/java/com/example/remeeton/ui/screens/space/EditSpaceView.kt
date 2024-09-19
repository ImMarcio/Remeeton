package com.example.remeeton.ui.screens.space

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import com.example.remeeton.model.data.Space
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.repository.SpaceDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.to


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSpaceView(
    spaceDAO: SpaceDAO,
    spaceId: String,
    onEditClick: (String) -> Unit
) {
    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId
    val scope = rememberCoroutineScope()
    var space by remember { mutableStateOf<Space?>(null) }
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    LaunchedEffect(spaceId) {
        spaceDAO.findById(spaceId) { result ->
            space = result
            if (result != null) {
                name = result.name
                description = result.description
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Editar espaço",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome do Espaço") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Descrição do Espaço") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (space != null) {
                    scope.launch(Dispatchers.IO) {
                        val newData = mapOf(
                            "name" to name,
                            "description" to description
                        )
                        spaceDAO.edit(spaceId, newData) { success ->
                            if (success) {
                                if (currentUserId != null) {
                                    onEditClick(currentUserId)
                                }
                            } else {
                                println("Falha ao editar o espaço.")
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
        ) {
            Text("Salvar", fontSize = 18.sp, color = Color.White)
        }
    }
}