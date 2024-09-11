package com.example.remeeton.model.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

//classe que pega dados do Firestore
 class  UserDAO {

    val db = FirebaseFirestore.getInstance()

    fun findAll(callback: (List<User>) -> Unit) {
        db.collection("users").get()
            .addOnSuccessListener { document ->
                val users = document.toObjects<User>()
                callback(users)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun findByName(name: String, callback: (User?) -> Unit) {
        db.collection("users").whereEqualTo("name", name).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val user = document.documents[0].toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun add(user: User, callback: (Boolean) -> Unit) {
        db.collection("users").add(user)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun update(user: User, callback: (Boolean) -> Unit) {
        user.id?.let { id ->
            db.collection("users").document(id).set(user)
                .addOnSuccessListener {
                    callback(true)
                }
                .addOnFailureListener {
                    callback(false)
                }
        } ?: callback(false)
    }

    fun deleteById(id: String, callback: (Boolean) -> Unit) {
        db.collection("users").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }
    fun findById(id: String, callback: (User?) -> Unit) {
        db.collection("users").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = document.toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    fun findByEmail(email: String, callback: (User?) -> Unit) {
        db.collection("users").whereEqualTo("email", email).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val user = document.documents[0].toObject<User>()
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun bookSpace(spaceId: String, userId: String, callback: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        val spaceRef = db.collection("spaces").document(spaceId)

        db.runTransaction { transaction ->
            val user = transaction.get(userRef).toObject<User>()
            val space = transaction.get(spaceRef).toObject<Space>()

            if (user != null && space != null) {
                if (space.reservedBy != null) {
                    throw Exception("O espaço já está reservado.")
                }
                space.reservedBy = userId
                transaction.set(spaceRef, space)

                val reservedSpaces = user.spaces.toMutableList()
                reservedSpaces.add(spaceId)
                user.spaces = reservedSpaces
                transaction.set(userRef, user)
            } else {
                throw Exception("Usuário ou espaço não encontrados.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun cancelBookingSpace(userId: String, spaceId: String, callback: (Boolean) -> Unit) {
        val userRef = db.collection("users").document(userId)
        val spaceRef = db.collection("spaces").document(spaceId)

        db.runTransaction { transaction ->
            val user = transaction.get(userRef).toObject<User>()
            val space = transaction.get(spaceRef).toObject<Space>()

            if (user != null && space != null) {
                if (space.reservedBy != userId) {
                    throw Exception("O espaço não está reservado por este usuário.")
                }
                space.reservedBy = null
                transaction.set(spaceRef, space)

                val reservedSpaces = user.spaces.toMutableList()
                reservedSpaces.remove(spaceId)
                user.spaces = reservedSpaces
                transaction.set(userRef, user)
            } else {
                throw Exception("Usuário ou espaço não encontrados.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }
}