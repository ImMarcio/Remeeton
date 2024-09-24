package com.example.remeeton.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.remeeton.model.data.Space

@Composable
fun SpaceCard(
    space: Space,
    onBookSpace: () -> Unit,
    onCancelSpace: () -> Unit,
    onEditSpace: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = space.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            val statusText = if (space.reservedBy == null) "Disponível" else "Reservado"
            val statusColor = if (space.reservedBy == null) Color.Blue else Color.Red
            Text(
                text = statusText,
                color = statusColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                if (space.reservedBy == null) {
                    Button(
                        onClick = onBookSpace,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
                    ) {
                        Text("Reservar", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onCancelSpace,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar Reserva", color = Color.White)
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onEditSpace,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Editar")
                }
            }
        }
    }
}
