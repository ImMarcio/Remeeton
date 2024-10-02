package com.example.remeeton.model.data.firestore

import com.google.firebase.firestore.DocumentId

data class User(
    @DocumentId
    val id: String? = null,
    var name: String = "",
    var email: String = "",
    var password: String = "",
)