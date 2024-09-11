package com.example.remeeton.model.data

import com.google.firebase.firestore.DocumentId

data class Space (
    @DocumentId
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val reserved: Boolean = false,
    var reservedBy: String? = null
)