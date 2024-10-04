package com.example.remeeton.model.data.firestore

import com.google.firebase.firestore.DocumentId

data class Booking(
    @DocumentId
    val id: String = "",
    val space: SpaceReference = SpaceReference(),
    val user: UserReference = UserReference(),
    val startTime: String = "",
    val endTime: String = "",
    val created: String = "",
    var reservationDate: String = ""
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