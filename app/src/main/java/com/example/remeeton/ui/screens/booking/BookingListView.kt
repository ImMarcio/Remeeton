package com.example.remeeton.ui.screens.booking

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.firestore.Booking
import com.example.remeeton.model.repository.firestore.BookingDAO

@Composable
fun BookingsListView() {
    val context = LocalContext.current
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var bookings by remember { mutableStateOf(emptyList<Booking>()) }
    val bookingDAO = BookingDAO()

    Log.d("BookingsListView", "Current User ID: $currentUserId")

    LaunchedEffect(currentUserId) {
        if (!currentUserId.isNullOrEmpty()) {
            bookingDAO.findBookingsByUser(currentUserId) { fetchedBookings ->
                bookings = fetchedBookings
                Log.d("BookingsListView", "Reservas carregadas: ${bookings.size}")
                bookings.forEach { booking ->
                    Log.d("BookingsListView", "Reserva ID: ${booking.id}, Espaço: ${booking.space.name}")
                }
            }
        } else {
            Log.d("BookingsListView", "Current User ID está nulo ou vazio.")
        }
    }

    LazyColumn {
        items(bookings) { booking ->
            BookingItem(booking) // Função para exibir cada item de reserva
        }

        // Mensagem para quando não houver reservas
        if (bookings.isEmpty()) {
            item {
                Text(
                    text = "Nenhuma reserva encontrada.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun BookingItem(booking: Booking) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // Adiciona uma sombra ao cartão
        shape = MaterialTheme.shapes.medium // Forma do cartão
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Nome do Espaço
            Text(
                text = "Nome do Espaço: ${booking.space.name}",
                style = MaterialTheme.typography.titleMedium, // Estilo de texto
                color = MaterialTheme.colorScheme.primary // Cor do texto
            )

            Spacer(modifier = Modifier.height(8.dp)) // Espaçamento entre os textos

            // Data de Início
            Text(
                text = "Data de Início: ${booking.startTime}",
                style = MaterialTheme.typography.bodyMedium, // Estilo de texto
                color = MaterialTheme.colorScheme.onSurface // Cor do texto
            )

            // Data de Término
            Text(
                text = "Data de Término: ${booking.endTime}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))


        }
    }
}

