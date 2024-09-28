package com.example.remeeton.model.data.firestore

import com.google.firebase.firestore.DocumentId
import java.util.Date

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
        val startTime: Date = Date(),
        val endTime: Date = Date()
    ) {
        override fun toString(): String {
            return "$startTime - $endTime"
        }
    }
}
