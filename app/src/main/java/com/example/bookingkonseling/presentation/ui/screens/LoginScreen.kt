// presentation/ui/screens/LoginScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bookingkonseling.R
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel

// presentation/ui/screens/LoginScreen.kt (bagian yang perlu diubah)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel
) {
    // State untuk form input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Observe UI state dari authViewModel yang di-pass
    val uiState by authViewModel.uiState.collectAsState()

    // PERBAIKAN: Hapus LaunchedEffect untuk auto navigation
    // Navigation akan ditangani oleh Navigation.kt

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // ... rest of the UI code remains the same ...

        // Logo aplikasi
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo Aplikasi",
            modifier = Modifier
                .size(120.dp)
                .padding(8.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "TELEMEDICINE",
            style = MaterialTheme.typography.headlineMedium,
            color = Color(0xFF1E3A5F),
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Booking Konseling",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Input Email
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                if (uiState.errorMessage != null) {
                    authViewModel.clearError()
                }
            },
            label = { Text("Email") },
            placeholder = { Text("Masukkan email Anda") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.errorMessage != null && email.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Input Password
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                if (uiState.errorMessage != null) {
                    authViewModel.clearError()
                }
            },
            label = { Text("Password") },
            placeholder = { Text("Masukkan password Anda") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !uiState.isLoading,
            isError = uiState.errorMessage != null && password.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tombol Login
        Button(
            onClick = {
                if (email.isNotBlank() && password.isNotBlank()) {
                    authViewModel.login(email.trim(), password)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1E3A5F),
                contentColor = Color.White
            )
        ) {
            if (uiState.isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Masuk...")
                }
            } else {
                Text(
                    "MASUK DENGAN AKUN UB",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Link ke Register
        TextButton(
            onClick = onNavigateToRegister,
            enabled = !uiState.isLoading
        ) {
            Text(
                "Belum punya akun? Daftar",
                color = Color(0xFF1E3A5F)
            )
        }

        // Error message
        uiState.errorMessage?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFEBEE)
                )
            ) {
                Text(
                    text = when {
                        error.contains("password") -> "Password salah. Silakan coba lagi."
                        error.contains("user") -> "Email tidak terdaftar."
                        error.contains("network") -> "Periksa koneksi internet Anda."
                        error.contains("invalid-email") -> "Format email tidak valid."
                        error.contains("too-many-requests") -> "Terlalu banyak percobaan. Coba lagi nanti."
                        else -> "Login gagal. Periksa email dan password Anda."
                    },
                    color = Color(0xFFD32F2F),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer info
        Text(
            text = "Gunakan email dan password akun UB Anda",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
    }
}