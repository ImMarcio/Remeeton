package com.example.remeeton.model.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_history")
data class BookingHistory(
    @PrimaryKey
    val bookingId: String,
    val userId: String,
    val spaceId: String,
    val spaceName: String,
    val startTime: Long,
    val endTime: Long,
    val status: String,
    val lastUpdated: Long
)
