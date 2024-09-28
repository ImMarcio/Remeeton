package com.example.remeeton.model.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_spaces")
data class FavoriteSpace(
        @PrimaryKey val id: String,
        val userId: String,
        val spaceName: String,
        val timestamp: Long
)