// data/repository/AdminRepository.kt
package com.example.bookingkonseling.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.data.model.BookingStats
import com.example.bookingkonseling.data.model.User
import kotlinx.coroutines.tasks.await
import java.util.*

class AdminRepository {
    private val firestore = FirebaseFirestore.getInstance()

    // Mendapatkan semua booking dengan filter
    suspend fun getAllBookings(status: String? = null): Result<List<Booking>> {
        return try {
            var query: Query = firestore.collection("bookings")
                .orderBy("createdAt", Query.Direction.DESCENDING)

            if (status != null && status != "All") {
                query = query.whereEqualTo("status", status)
            }

            val querySnapshot = query.get().await()
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

    // Update status booking
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

    // Mendapatkan statistik booking
    suspend fun getBookingStats(): Result<BookingStats> {
        return try {
            val allBookings = firestore.collection("bookings").get().await()
            val bookings = allBookings.documents.mapNotNull { document ->
                try {
                    document.toObject(Booking::class.java)
                } catch (e: Exception) {
                    null
                }
            }

            val now = Calendar.getInstance()
            val todayStart = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val weekStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val monthStart = Calendar.getInstance().apply {
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val stats = BookingStats(
                totalBookings = bookings.size,
                pendingBookings = bookings.count { it.status == "Pending" },
                ongoingBookings = bookings.count { it.status == "Ongoing" },
                completedBookings = bookings.count { it.status == "Completed" },
                cancelledBookings = bookings.count { it.status == "Cancelled" },
                todayBookings = bookings.count {
                    it.createdAt.toDate().after(todayStart.time)
                },
                thisWeekBookings = bookings.count {
                    it.createdAt.toDate().after(weekStart.time)
                },
                thisMonthBookings = bookings.count {
                    it.createdAt.toDate().after(monthStart.time)
                }
            )

            Result.success(stats)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Mendapatkan semua user/mahasiswa
    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val querySnapshot = firestore.collection("users").get().await()
            val users = querySnapshot.documents.mapNotNull { document ->
                try {
                    document.toObject(User::class.java)
                } catch (e: Exception) {
                    null
                }
            }

            Result.success(users)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Assign konselor ke booking
    suspend fun assignKonselor(bookingId: String, konselorName: String): Result<Unit> {
        return try {
            firestore.collection("bookings")
                .document(bookingId)
                .update(
                    mapOf(
                        "konselor" to konselorName,
                        "status" to "Ongoing"
                    )
                )
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}