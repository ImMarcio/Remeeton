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

    fun reservarSala(salaId: String, usuarioId: String, callback: (Boolean) -> Unit) {
        val salaRef = db.collection("salas").document(salaId)

        db.runTransaction { transaction ->
            val sala = transaction.get(salaRef).toObject<Room>()
            if (sala != null) {
                // Verifica se a sala já está reservada
                if (sala.reservadoPor != null) {
                    throw Exception("Sala já está reservada.")
                }

                // Atualiza o campo reservadoPor com o ID do usuário

                sala.reservadoPor = usuarioId
                transaction.set(salaRef, sala)
            } else {
                throw Exception("Sala não encontrada.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun cancelarReservaSala(salaId: String, callback: (Boolean) -> Unit) {
        val salaRef = db.collection("salas").document(salaId)

        db.runTransaction { transaction ->
            val sala = transaction.get(salaRef).toObject<Room>()
            if (sala != null) {
                // Verifica se a sala não está reservada
                if (sala.reservadoPor == null) {
                    throw Exception("Sala não está reservada.")
                }

                // Limpa o campo reservadoPor
                sala.reservadoPor = null
                transaction.set(salaRef, sala)
            } else {
                throw Exception("Sala não encontrada.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

}