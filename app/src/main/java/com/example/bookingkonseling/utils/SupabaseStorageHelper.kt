// utils/SupabaseStorageHelper.kt
package com.example.bookingkonseling.utils

import android.content.Context
import android.net.Uri
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

class SupabaseStorageHelper(private val context: Context) {

    private val storage = SupabaseClient.client.storage
    private val bucketName = Constants.KTM_BUCKET

    /**
     * Upload file ke Supabase Storage
     * @param uri URI file yang akan diupload
     * @param folder Folder tujuan di storage
     * @return Result dengan URL file yang diupload
     */
    suspend fun uploadFile(uri: Uri, folder: String = "ktm"): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                // Generate nama file unik
                val fileExtension = getFileExtension(uri)
                val fileName = "${folder}/${UUID.randomUUID()}.$fileExtension"

                // Baca file dari URI
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Upload ke Supabase Storage
                    storage.from(bucketName).upload(fileName, bytes, upsert = false)

                    // Dapatkan public URL
                    val publicUrl = storage.from(bucketName).publicUrl(fileName)

                    println("File uploaded successfully: $publicUrl")
                    Result.success(publicUrl)
                } else {
                    Result.failure(Exception("Gagal membaca file"))
                }
            }
        } catch (e: Exception) {
            println("Upload error: ${e.message}")
            Result.failure(e)
        }
    }

    /**
     * Update file yang sudah ada
     */
    suspend fun updateFile(uri: Uri, fileName: String): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Update file dengan upsert = true
                    storage.from(bucketName).upload(fileName, bytes, upsert = true)

                    val publicUrl = storage.from(bucketName).publicUrl(fileName)
                    Result.success(publicUrl)
                } else {
                    Result.failure(Exception("Gagal membaca file"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Hapus file dari Supabase Storage
     */
    suspend fun deleteFile(fileName: String): Result<Unit> {
        return try {
            withContext(Dispatchers.IO) {
                storage.from(bucketName).delete(fileName)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Dapatkan URL file
     */
    fun getFileUrl(fileName: String): String {
        return storage.from(bucketName).publicUrl(fileName)
    }

    /**
     * Get file extension from URI
     */
    private fun getFileExtension(uri: Uri): String {
        return when (context.contentResolver.getType(uri)) {
            "image/jpeg" -> "jpg"
            "image/jpg" -> "jpg"
            "image/png" -> "png"
            "image/gif" -> "gif"
            "image/webp" -> "webp"
            else -> "jpg" // default
        }
    }

    /**
     * Validate file size (max 5MB)
     */
    suspend fun validateFileSize(uri: Uri, maxSizeMB: Int = 5): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                val inputStream = context.contentResolver.openInputStream(uri)
                val size = inputStream?.available() ?: 0
                inputStream?.close()

                val maxSizeBytes = maxSizeMB * 1024 * 1024
                size <= maxSizeBytes
            }
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Validate file type
     */
    fun validateFileType(uri: Uri): Boolean {
        val mimeType = context.contentResolver.getType(uri)
        return mimeType?.startsWith("image/") == true
    }
}