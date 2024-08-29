package com.example.navegacao1.model.dados

import com.google.firebase.firestore.DocumentId

data class Usuario(
    @DocumentId
    val id: String? = null,
    var nome: String = "",
    var email: String = "",
    val senha: String = "",
    val salas: List<String> = mutableListOf()
)