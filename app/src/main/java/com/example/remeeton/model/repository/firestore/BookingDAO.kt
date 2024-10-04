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