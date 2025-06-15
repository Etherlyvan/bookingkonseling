// data/repository/BookingRepository.kt
package com.example.bookingkonseling.data.repository

import android.content.Context
import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.utils.SupabaseStorageHelper
import kotlinx.coroutines.tasks.await

class BookingRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageHelper = SupabaseStorageHelper(context)

    suspend fun createBooking(booking: Booking, ktmUri: Uri?): Result<String> {
        return try {
            var bookingData = booking
            var uploadedFileUrl: String? = null

            // Upload KTM jika ada
            if (ktmUri != null) {
                // Validasi file
                if (!storageHelper.validateFileType(ktmUri)) {
                    return Result.failure(Exception("Format file tidak didukung. Gunakan JPG, PNG, atau GIF."))
                }

                if (!storageHelper.validateFileSize(ktmUri, 5)) {
                    return Result.failure(Exception("Ukuran file terlalu besar. Maksimal 5MB."))
                }

                // Upload file
                val uploadResult = storageHelper.uploadFile(ktmUri, "ktm")
                uploadResult.fold(
                    onSuccess = { url ->
                        uploadedFileUrl = url
                        bookingData = bookingData.copy(ktmUrl = url)
                        println("KTM uploaded successfully: $url")
                    },
                    onFailure = { exception ->
                        return Result.failure(Exception("Gagal upload KTM: ${exception.message}"))
                    }
                )
            }

            // Simpan booking ke Firestore
            val bookingId = firestore.collection("bookings").document().id
            bookingData = bookingData.copy(id = bookingId)

            firestore.collection("bookings")
                .document(bookingId)
                .set(bookingData)
                .await()

            println("Booking created successfully with ID: $bookingId")
            if (uploadedFileUrl != null) {
                println("KTM URL: $uploadedFileUrl")
            }

            Result.success(bookingId)
        } catch (e: Exception) {
            println("Create booking error: ${e.message}")
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
            // Get booking data first to delete KTM file
            val bookingDoc = firestore.collection("bookings")
                .document(bookingId)
                .get()
                .await()

            val booking = bookingDoc.toObject(Booking::class.java)

            // Delete KTM file if exists
            booking?.ktmUrl?.let { ktmUrl ->
                if (ktmUrl.isNotEmpty()) {
                    try {
                        // Extract filename from URL
                        val fileName = ktmUrl.substringAfterLast("/")
                        storageHelper.deleteFile("ktm/$fileName")
                    } catch (e: Exception) {
                        println("Error deleting KTM file: ${e.message}")
                        // Continue with booking deletion even if file deletion fails
                    }
                }
            }

            // Delete booking document
            firestore.collection("bookings")
                .document(bookingId)
                .delete()
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateBooking(booking: Booking, newKtmUri: Uri?): Result<Unit> {
        return try {
            var updatedBooking = booking

            // Upload new KTM if provided
            if (newKtmUri != null) {
                if (!storageHelper.validateFileType(newKtmUri)) {
                    return Result.failure(Exception("Format file tidak didukung."))
                }

                if (!storageHelper.validateFileSize(newKtmUri, 5)) {
                    return Result.failure(Exception("Ukuran file terlalu besar."))
                }

                val uploadResult = storageHelper.uploadFile(newKtmUri, "ktm")
                uploadResult.fold(
                    onSuccess = { url ->
                        updatedBooking = updatedBooking.copy(ktmUrl = url)
                    },
                    onFailure = { exception ->
                        return Result.failure(Exception("Gagal upload KTM: ${exception.message}"))
                    }
                )
            }

            // Update booking
            firestore.collection("bookings")
                .document(booking.id)
                .set(updatedBooking)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}