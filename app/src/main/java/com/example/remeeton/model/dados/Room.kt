package com.example.remeeton.model.dados

import com.google.firebase.firestore.DocumentId

data class Room (
    @DocumentId
    val id: String? = null,
    val nome: String = "",
    val descricao: String = "",
    val reservada: Boolean = false,
    var reservadoPor: String? = null
)