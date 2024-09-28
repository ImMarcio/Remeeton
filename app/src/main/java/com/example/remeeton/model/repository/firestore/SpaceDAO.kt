package com.example.remeeton.model.repository.firestore

import com.example.remeeton.model.data.firestore.Space
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects

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
}