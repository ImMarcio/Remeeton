package com.example.remeeton.model.data.firestore

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class Booking(
    @DocumentId
    val id: String = "",
    val space: SpaceReference = SpaceReference(),
    val user: UserReference = UserReference(),
    val startTime: Date = Date(),
    val endTime: Date = Date(System.currentTimeMillis() + 3600000),
    val status: String = "reservado"
) {
    data class SpaceReference(
        val id: String = "",
        val name: String = ""
    )

    data class UserReference(
        val id: String = "",
        val name: String = ""
    )
}