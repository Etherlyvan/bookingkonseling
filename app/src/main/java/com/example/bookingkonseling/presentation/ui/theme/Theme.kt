// presentation/ui/theme/Theme.kt
package com.example.bookingkonseling.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// PERBAIKAN: Force light theme, tidak terpengaruh sistem
private val AppColorScheme = lightColorScheme(
    primary = Color(0xFF1E3A5F),
    secondary = Color(0xFFFF9800),
    tertiary = Color(0xFF6B4EFF),
    background = Color(0xFFF5F5F5),
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    error = Color(0xFFE57373),
    onError = Color.White
)

@Composable
fun BookingKonselingTheme(
    darkTheme: Boolean = false, // PERBAIKAN: Selalu false untuk force light mode
    content: @Composable () -> Unit
) {
    // PERBAIKAN: Selalu gunakan light color scheme
    MaterialTheme(
        colorScheme = AppColorScheme,
        content = content
    )
}