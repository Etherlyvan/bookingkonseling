// utils/SupabaseClient.kt
package com.example.bookingkonseling.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.storage.Storage

object SupabaseClient {
    val client = createSupabaseClient(
        supabaseUrl = Constants.SUPABASE_URL,
        supabaseKey = Constants.SUPABASE_ANON_KEY
    ) {
        install(Storage)
    }
}