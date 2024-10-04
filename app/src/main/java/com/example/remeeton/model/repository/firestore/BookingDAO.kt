package com.example.remeeton.model.repository.firestore

import android.util.Log
import com.example.remeeton.model.data.firestore.Booking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

class BookingDAO {
    val db = FirebaseFirestore.getInstance()

    fun addBooking(booking: Booking, callback: (Boolean) -> Unit) {
        db.collection("bookings").add(booking)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun cancelBooking(bookingId: String, callback: (Boolean) -> Unit) {
        db.collection("bookings").document(bookingId).update("status", "cancelado")
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun findBookingsByUserId(userId: String, onResult: (List<Booking>) -> Unit) {
        Log.d("BookingDAO", "Buscando reservas para o userId: $userId")
        db.collection("bookings")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("BookingDAO", "Documentos encontrados: ${documents.size()}")
                val bookings = documents.map { doc ->
                    doc.toObject(Booking::class.java)
                }
                onResult(bookings)
            }
            .addOnFailureListener { exception ->
                Log.e("BookingDAO", "Error getting bookings: ", exception)
                onResult(emptyList())
            }
    }


    fun findBookingsByUser(userId: String, callback: (List<Booking>) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("user.id", userId)
            .get()
            .addOnSuccessListener { documents ->
                val bookings = mutableListOf<Booking>()
                for (document in documents) {
                    val booking = document.toObject(Booking::class.java)
                    bookings.add(booking)
                }
                callback(bookings)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    fun findBookingsBySpace(spaceId: String, callback: (List<Booking>) -> Unit) {
        db.collection("bookings")
            .whereEqualTo("space.id", spaceId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val bookings = querySnapshot.toObjects(Booking::class.java)
                callback(bookings)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}