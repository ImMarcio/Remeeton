package com.example.remeeton.model.dados

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

//classe que pega dados do Firestore
 class  UsuarioDAO {

    val db = FirebaseFirestore.getInstance()

    fun buscarUsuarios(callback: (List<Usuario>) -> Unit) {
        db.collection("usuarios").get()
            .addOnSuccessListener { document ->
                val usuarios = document.toObjects<Usuario>()
                callback(usuarios)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun buscarUsuarioPorNome(nome: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").whereEqualTo("nome", nome).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val usuario = document.documents[0].toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun adicionar(usuario: Usuario, callback: (Boolean) -> Unit) {
        db.collection("usuarios").add(usuario)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun atualizarUsuario(usuario: Usuario, callback: (Boolean) -> Unit) {
        usuario.id?.let { id ->
            db.collection("usuarios").document(id).set(usuario)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } ?: callback(false)
    }

    fun excluirUsuarioPorId(id: String, callback: (Boolean) -> Unit) {
        db.collection("usuarios").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    fun buscarUsuarioPorId(id: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    fun buscarUsuarioPorEmail(email: String, callback: (Usuario?) -> Unit) {
        db.collection("usuarios").whereEqualTo("email", email).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val usuario = document.documents[0].toObject<Usuario>()
                    callback(usuario)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    // Função para reservar uma sala
    fun reservarSala(salaId: String, usuarioId: String, callback: (Boolean) -> Unit) {
        val usuarioRef = db.collection("usuarios").document(usuarioId)
        val salaRef = db.collection("salas").document(salaId)

        db.runTransaction { transaction ->
            val usuario = transaction.get(usuarioRef).toObject<Usuario>()
            val sala = transaction.get(salaRef).toObject<Room>()

            if (usuario != null && sala != null) {
                // Verifica se a sala já está reservada
                if (sala.reservadoPor != null) {
                    throw Exception("Sala já está reservada.")
                }

                // Atualiza o campo reservadoPor da sala
                sala.reservadoPor = usuarioId
                transaction.set(salaRef, sala)

                // Adiciona a sala à lista de salas reservadas do usuário
                val salasReservadas = usuario.salas.toMutableList()
                salasReservadas.add(salaId)
                usuario.salas = salasReservadas
                transaction.set(usuarioRef, usuario)
            } else {
                throw Exception("Usuário ou sala não encontrados.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    // Novo método para cancelar a reserva de uma sala
    fun cancelarReservaSala(usuarioId: String, salaId: String, callback: (Boolean) -> Unit) {
        val usuarioRef = db.collection("usuarios").document(usuarioId)
        val salaRef = db.collection("salas").document(salaId)

        db.runTransaction { transaction ->
            val usuario = transaction.get(usuarioRef).toObject<Usuario>()
            val sala = transaction.get(salaRef).toObject<Room>()

            if (usuario != null && sala != null) {
                // Verifica se a sala está reservada pelo usuário
                if (sala.reservadoPor != usuarioId) {
                    throw Exception("Sala não está reservada por este usuário.")
                }

                // Limpa o campo reservadoPor da sala
                sala.reservadoPor = null
                transaction.set(salaRef, sala)

                // Remove a sala da lista de salas reservadas do usuário
                val salasReservadas = usuario.salas.toMutableList()
                salasReservadas.remove(salaId)
                usuario.salas = salasReservadas
                transaction.set(usuarioRef, usuario)
            } else {
                throw Exception("Usuário ou sala não encontrados.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }


}