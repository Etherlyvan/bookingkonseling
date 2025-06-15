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
     */
    suspend fun uploadFile(uri: Uri, folder: String = "ktm"): Result<String> {
        return try {
            withContext(Dispatchers.IO) {
                // Generate nama file unik
                val fileName = "${folder}/${UUID.randomUUID()}.jpg"

                // Baca file dari URI
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    // Upload ke Supabase Storage
                    storage.from(bucketName).upload(fileName, bytes)

                    // Dapatkan public URL
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
}