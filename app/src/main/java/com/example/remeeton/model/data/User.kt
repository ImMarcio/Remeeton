package com.example.remeeton.model.data

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String? = null,
    var name: String = "",
    var email: String = "",
    val password: String = "",
    var spaces: List<String> = mutableListOf()
)