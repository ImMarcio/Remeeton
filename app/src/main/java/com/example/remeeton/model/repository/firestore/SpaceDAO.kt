package com.example.remeeton.model.repository.firestore

import com.example.remeeton.model.data.firestore.Space
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

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
            .addOnSuccessListener { documents ->
                val spaces = documents.mapNotNull { document ->
                    val spaceData = document.data

                    val availabilityList = spaceData["availability"] as? List<Map<String, Any>>
                    val availabilities = availabilityList?.mapNotNull { availabilityMap ->
                        val startTimeMap = availabilityMap["startTime"]
                        val endTimeMap = availabilityMap["endTime"]

                        val startTime = if (startTimeMap is com.google.firebase.Timestamp) {
                            startTimeMap
                        } else {
                            (startTimeMap as? HashMap<String, Any>)?.let {
                                val seconds = it["seconds"] as? Long
                                val nanoseconds = it["nanoseconds"] as? Int

                                if (seconds != null && nanoseconds != null) {
                                    com.google.firebase.Timestamp(seconds, nanoseconds)
                                } else {
                                    null
                                }
                            }
                        }

                        val endTime = if (endTimeMap is com.google.firebase.Timestamp) {
                            endTimeMap
                        } else {
                            (endTimeMap as? HashMap<String, Any>)?.let {
                                val seconds = it["seconds"] as? Long
                                val nanoseconds = it["nanoseconds"] as? Int

                                if (seconds != null && nanoseconds != null) {
                                    com.google.firebase.Timestamp(seconds, nanoseconds)
                                } else {
                                    null
                                }
                            }
                        }


                        if (startTime != null && endTime != null) {
                            Space.Availability(startTime, endTime)
                        } else {
                            null
                        }
                    } ?: emptyList()

                    // Verificação de nulos no campo capacity
                    val capacity = (spaceData["capacity"] as? Long)?.toInt() ?: 0  // Usa 0 se for null

                    // Criação do objeto Space manualmente
                    Space(
                        name = spaceData["name"] as? String ?: "",
                        description = spaceData["description"] as? String ?: "",
                        location = document.toObject(Space.Location::class.java),
                        capacity = capacity,  // Usa o valor verificado
                        availability = availabilities,
                        images = (spaceData["images"] as? List<String>)?.map { it.trim() } ?: emptyList()
                    )
                }
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