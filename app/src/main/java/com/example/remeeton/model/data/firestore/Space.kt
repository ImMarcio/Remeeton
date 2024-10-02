package com.example.remeeton.model.data.firestore

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import java.io.Serializable

data class Space(
    @DocumentId
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val location: Location = Location(),
    val capacity: Int = 0,
    var availability: List<Availability> = listOf(),
    val images: List<String> = listOf(),
    var isReserved: Boolean = false
) {
    data class Location(
        val address: String = "",
        val latitude: Double = 0.0,
        val longitude: Double = 0.0
    )

    data class Availability(
        val startTime: Timestamp  = Timestamp.now(),
        val endTime: Timestamp  = Timestamp.now()
    )  : Serializable
    {
        override fun toString(): String {
            val formattedStartTime = startTime.toDate()
            val formattedEndTime = endTime.toDate()
            return "$formattedStartTime - $formattedEndTime"
        }
    }
}

