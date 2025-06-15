// data/model/Admin.kt
package com.example.bookingkonseling.data.model

data class Admin(
    val uid: String = "",
    val nama: String = "",
    val email: String = "",
    val role: String = "admin",
    val createdAt: com.google.firebase.Timestamp = com.google.firebase.Timestamp.now()
)

