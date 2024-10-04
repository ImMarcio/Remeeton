import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

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
            
            AsyncImage(
                model = space.images.firstOrNull(), // Pega a primeira imagem do array
                contentDescription = "Imagem do espaço: ${space.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Defina a altura que desejar para a imagem
            )

            Text(
                text = space.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Verifica se o espaço está reservado
            val isCurrentlyReserved = space.isReserved
            val statusText = if (isCurrentlyReserved) {
                "Reservado"
            } else {
                "Disponível"
            }

            val statusColor = if (isCurrentlyReserved) {
                Color.Red
            } else {
                Color.Gray
            }

            Text(
                text = statusText,
                color = statusColor,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                if (isCurrentlyReserved) {
                    Button(
                        onClick = onCancelSpace,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Cancelar Reserva", color = Color.White)
                    }
                } else {
                    Button(
                        onClick = onBookSpace ,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
                    ){
                        Text("Reservar", color = Color.Blue)
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
