package com.example.remeeton.model.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_spaces")
data class CachedSpace(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val lastUpdated: Long
)
