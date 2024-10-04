package com.example.remeeton.ui.screens

import Space
import SpaceCard
import SpaceDAO
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remeeton.model.data.PreferencesUtil
import com.example.remeeton.model.data.firestore.Booking
import com.example.remeeton.model.repository.firestore.BookingDAO
import com.example.remeeton.ui.components.MessageHandler
import com.example.remeeton.ui.components.SearchBar
import com.example.remeeton.ui.screens.booking.BookingsListView
import com.google.firebase.Timestamp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun Home(
    navController: NavController,
    modifier: Modifier = Modifier,
    onLogoffClick: () -> Unit,
    onNavigateToBookings: () -> Unit,
    userId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val preferencesUtil = remember { PreferencesUtil(context) }
    val currentUserId = preferencesUtil.currentUserId

    var spaces by remember { mutableStateOf<List<Space>>(emptyList()) }
    var searchQuery by remember { mutableStateOf("") }
    var messageError by remember { mutableStateOf<String?>(null) }
    var messageSuccess by remember { mutableStateOf<String?>(null) }

    val spaceDAO = SpaceDAO()
    val bookingDAO = BookingDAO()


    fun loadSpaces() {
        scope.launch(Dispatchers.IO) {
            spaceDAO.findAll { returnedSpaces -> spaces = returnedSpaces }
        }
    }


    fun String.toTimestamp(): Timestamp {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = dateFormat.parse(this)
        return Timestamp(date)
    }

    fun bookSpace(space: Space) {
        // Verifica a disponibilidade do espaço baseado em horários
        val currentTime = Timestamp.now()
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        val currentHour = sdf.format(currentTime.toDate())

//        if (currentHour < space.startTime || currentHour > space.endTime) {
//            messageError = "Este espaço não está disponível neste horário."
//            return
//        }

        val newStartTime = currentTime
        val newEndTime = Timestamp(Date(newStartTime.toDate().time + 3600000 )) //3600000  Reserva por 1 hora

        bookingDAO.findBookingsBySpace(space.id) { existingBookings ->
            val isOverlapping = existingBookings.any { booking ->
                val bookingStart = booking.startTime.toTimestamp()
                val bookingEnd = booking.endTime.toTimestamp()
                (newStartTime.toDate() < bookingEnd.toDate() && newEndTime.toDate() > bookingStart.toDate())
            }

//            if (isOverlapping) {
//                messageError = "Este espaço já está reservado nesse horário."
//                return@findBookingsBySpace
//            }

            val booking = Booking(
                id = "",
                space = Booking.SpaceReference(id = space.id, name = space.name),
                user = Booking.UserReference(id = userId, name = preferencesUtil.currentUserId ?: "Usuário"),
                startTime = sdf.format(newStartTime.toDate()),
                endTime = sdf.format(newEndTime.toDate())
            )

            bookingDAO.addBooking(booking) { success ->
                if (success) {
                    messageSuccess = "Espaço reservado com sucesso."
                    loadSpaces() // Atualiza a lista de espaços após a reserva

                } else {
                    messageError = "Falha ao reservar o espaço."
                }
            }
        }
    }

    fun cancelBooking(spaceId: String) {
        bookingDAO.findBookingsByUser(userId) { bookings ->
            val userBookings = bookings.filter { it.space.id == spaceId }
            if (userBookings.isNotEmpty()) {
                userBookings.forEach { booking ->
                    bookingDAO.cancelBooking(booking.id) { success ->
                        if (success) {
                            messageSuccess = "Reserva cancelada com sucesso."
                            loadSpaces() // Atualiza a lista de espaços após o cancelamento
                        } else {
                            messageError = "Falha ao cancelar a reserva."
                        }
                    }
                }
            } else {
                messageError = "Nenhuma reserva encontrada para este espaço."
            }
        }
    }

    LaunchedEffect(Unit) {

        loadSpaces()


    }


    LaunchedEffect(messageSuccess, messageError) {
        messageSuccess?.let {
            delay(3000)
            messageSuccess = null
        }
        messageError?.let {
            delay(3000)
            messageError = null
        }
    }



    Column(modifier = modifier
        .fillMaxSize()
        .padding(16.dp)) {
        SearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it }
        )
    Row {
        Button(
            onClick = { navController.navigate("register-space") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE)),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Adicionar Espaço")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Adicionar")
        }
        Spacer(modifier = Modifier.width(4.dp))

        Button(
            onClick = { navController.navigate("bookings") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF18EE00)),
            modifier = Modifier.height(56.dp)
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "Suas Reservas")
            Spacer(modifier = Modifier.width(4.dp))
            Text("Ver Reservas")
        }
    }


        MessageHandler(
            messageSuccess = messageSuccess,
            messageError = messageError
        )

        LazyColumn {
            items(spaces.filter { it.name.contains(searchQuery, ignoreCase = true) }) { space ->
                SpaceCard(
                    space = space,
                    onBookSpace = { bookSpace(space) },
                    onCancelSpace = { cancelBooking(space.id) },
                    onEditSpace = { navController.navigate("edit-space/${space.id}") }
                )
            }

        }
    }
}
