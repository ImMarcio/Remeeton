package com.example.remeeton.model.repository

import com.example.remeeton.model.data.Space
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import java.lang.Exception

class SpaceDAO {
    val db = FirebaseFirestore.getInstance()

    fun add(space: Space, callback: (Boolean) -> Unit) {
        db.collection("spaces").add(space)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun findAll(callback: (List<Space>) -> Unit) {
        db.collection("spaces").get()
            .addOnSuccessListener { document ->
                val spaces = document.toObjects<Space>()
                callback(spaces)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun findByName(name: String, callback: (Space?) -> Unit) {
        db.collection("spaces").whereEqualTo("name", name).get()
            .addOnSuccessListener { document ->
                if (!document.isEmpty) {
                    val space = document.documents[0].toObject<Space>()
                    callback(space)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun deleteById(id: String, callback: (Boolean) -> Unit) {
        db.collection("spaces").document(id).delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun edit(id: String, newData: Map<String, Any>, callback: (Boolean) -> Unit) {
        db.collection("spaces").document(id).update(newData)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun findById(id: String, callback: (Space?) -> Unit) {
        db.collection("spaces").document(id).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val space = document.toObject<Space>()
                    callback(space)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun book(spaceId: String, userId: String, callback: (Boolean) -> Unit) {
        val spaceRef = db.collection("spaces").document(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()
            if (space != null) {
                if (space.reservedBy != null) {
                    throw Exception("O espaço já está reservado.")
                }
                space.reservedBy = userId
                transaction.set(spaceRef, space)
            } else {
                throw Exception("Espaço não encontrado.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

    fun cancelBooking(spaceId: String, callback: (Boolean) -> Unit) {
        val spaceRef = db.collection("spaces").document(spaceId)

        db.runTransaction { transaction ->
            val space = transaction.get(spaceRef).toObject<Space>()
            if (space != null) {
                if (space.reservedBy == null) {
                    throw Exception("O espaço já está reservado.")
                }
                space.reservedBy = null
                transaction.set(spaceRef, space)
            } else {
                throw Exception("Espaço não encontrado.")
            }
        }.addOnSuccessListener {
            callback(true)
        }.addOnFailureListener {
            callback(false)
        }
    }

}