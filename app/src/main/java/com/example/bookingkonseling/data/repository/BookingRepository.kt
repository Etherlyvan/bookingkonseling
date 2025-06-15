// data/repository/BookingRepository.kt
package com.example.bookingkonseling.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.bookingkonseling.data.model.Booking
import kotlinx.coroutines.tasks.await

class BookingRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createBooking(booking: Booking, ktmUri: Uri?): Result<String> {
        return try {
            var bookingData = booking

            // Jika ada KTM, upload ke storage (implementasi sesuai kebutuhan)
            if (ktmUri != null) {
                // TODO: Implement file upload
                // val uploadResult = uploadFile(ktmUri)
                // bookingData = bookingData.copy(ktmUrl = uploadResult)
            }

            val bookingId = firestore.collection("bookings").document().id
            bookingData = bookingData.copy(id = bookingId)

            firestore.collection("bookings")
                .document(bookingId)
                .set(bookingData)
                .await()

            Result.success(bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserBookings(userId: String): Result<List<Booking>> {
        return try {
            val querySnapshot = firestore.collection("bookings")
                .whereEqualTo("userId", userId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val bookings = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(Booking::class.java)
                } catch (e: Exception) {
                    // Log error tapi jangan crash
                    println("Error parsing booking: ${e.message}")
                    null
                }
            }

            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBookingStatus(bookingId: String, status: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update("status", status)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBooking(bookingId: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}