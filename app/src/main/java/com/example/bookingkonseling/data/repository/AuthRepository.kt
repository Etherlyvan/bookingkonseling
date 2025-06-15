// data/repository/AuthRepository.kt
package com.example.bookingkonseling.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.bookingkonseling.data.model.User
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers

class AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    // Mendapatkan user yang sedang login
    fun getCurrentUser(): FirebaseUser? {
        return try {
            auth.currentUser
        } catch (e: Exception) {
            println("Error getting current user: ${e.message}")
            null
        }
    }

    // Login dengan email dan password
    suspend fun login(email: String, password: String): Result<FirebaseUser> {
        return try {
            withContext(Dispatchers.IO) {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                if (result.user != null) {
                    Result.success(result.user!!)
                } else {
                    Result.failure(Exception("Login failed: User is null"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register user baru
    suspend fun register(
        email: String,
        password: String,
        nama: String,
        nim: String,
        prodi: String,
        nomorHP: String
    ): Result<FirebaseUser> {
        return try {
            withContext(Dispatchers.IO) {
                // Buat akun di Firebase Auth
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user

                if (user != null) {
                    // Simpan data user ke Firestore
                    val userData = User(
                        uid = user.uid,
                        nama = nama,
                        nim = nim,
                        prodi = prodi,
                        nomorHP = nomorHP,
                        email = email
                    )

                    firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .await()

                    Result.success(user)
                } else {
                    Result.failure(Exception("Registration failed: User is null"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // PERBAIKAN: Logout yang lebih robust
    fun logout() {
        try {
            // Sign out dari Firebase Auth
            auth.signOut()

            // Clear any cached data jika ada
            // Misalnya SharedPreferences, Room Database, dll

            println("Logout successful")
        } catch (e: Exception) {
            println("Logout error (non-critical): ${e.message}")
            // Tetap lanjutkan logout meskipun ada error
            try {
                auth.signOut() // Coba lagi
            } catch (e2: Exception) {
                println("Second logout attempt failed: ${e2.message}")
            }
        }
    }

    // Mendapatkan data user dari Firestore
    suspend fun getUserData(uid: String): Result<User> {
        return try {
            withContext(Dispatchers.IO) {
                val document = firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()

                val user = document.toObject(User::class.java)
                if (user != null) {
                    Result.success(user)
                } else {
                    Result.failure(Exception("User data not found"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}