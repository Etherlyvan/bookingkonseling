// data/model/BookingStats.kt
package com.example.bookingkonseling.data.model

data class BookingStats(
    val totalBookings: Int = 0,
    val pendingBookings: Int = 0,
    val ongoingBookings: Int = 0,
    val completedBookings: Int = 0,
    val cancelledBookings: Int = 0,
    val todayBookings: Int = 0,
    val thisWeekBookings: Int = 0,
    val thisMonthBookings: Int = 0
)