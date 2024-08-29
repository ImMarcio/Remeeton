package com.example.navegacao1.model.dados

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

class RoomDao {
    val db = FirebaseFirestore.getInstance()



    fun adicionar(sala: Room, callback: (Boolean) -> Unit) {
        db.collection("salas").add(sala)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun buscarSalas(callback: (List<Room>) -> Unit) {
        db.collection("salas").get()
            .addOnSuccessListener { document ->
                val rooms = document.toObjects<Room>()
                callback(rooms)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarSalaPorNome(nome: String, callback: (Room?) -> Unit) {
        db.collection("salas").whereEqualTo("nome", nome).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val sala = document.documents[0].toObject<Room>()
                    callback(sala)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun excluirSalaPorId(id: String, callback: (Boolean) -> Unit) {
        db.collection("salas").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun editarSala(id: String, novosDados: Map<String, Any>, callback: (Boolean) -> Unit) {
        db.collection("salas").document(id).update(novosDados)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun buscarSalaPorId(id: String, callback: (Room?) -> Unit) {
        db.collection("salas").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val sala = document.toObject<Room>()
                    callback(sala)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

}