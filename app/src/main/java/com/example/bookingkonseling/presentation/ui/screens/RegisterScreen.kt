// presentation/ui/screens/RegisterScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    authViewModel: AuthViewModel // PERBAIKAN: Gunakan parameter authViewModel
) {
    // State untuk form
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var nomorHP by remember { mutableStateOf("") }

    // State untuk validasi
    var isPasswordMatch by remember { mutableStateOf(true) }
    var isEmailValid by remember { mutableStateOf(true) }
    var isNIMValid by remember { mutableStateOf(true) }

    // Observe UI state dari authViewModel yang di-pass
    val uiState by authViewModel.uiState.collectAsState()

    // Validasi email format
    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Validasi NIM (harus angka dan minimal 10 digit)
    fun isValidNIM(nim: String): Boolean {
        return nim.all { it.isDigit() } && nim.length >= 10
    }

    // Update validasi saat input berubah
    LaunchedEffect(email) {
        isEmailValid = email.isEmpty() || isValidEmail(email)
    }

    LaunchedEffect(nim) {
        isNIMValid = nim.isEmpty() || isValidNIM(nim)
    }

    LaunchedEffect(password, confirmPassword) {
        isPasswordMatch = confirmPassword.isEmpty() || password == confirmPassword
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Daftar Akun",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !uiState.isLoading
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A5F),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Buat Akun Baru",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E3A5F)
            )

            Text(
                text = "Lengkapi data diri Anda",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Form fields
            OutlinedTextField(
                value = nama,
                onValueChange = {
                    nama = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Nama Lengkap") },
                placeholder = { Text("Masukkan nama lengkap") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nim,
                onValueChange = {
                    nim = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("NIM") },
                placeholder = { Text("Masukkan NIM (minimal 10 digit)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = !isNIMValid,
                supportingText = {
                    if (!isNIMValid) {
                        Text(
                            text = "NIM harus berupa angka minimal 10 digit",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = prodi,
                onValueChange = {
                    prodi = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Program Studi") },
                placeholder = { Text("Masukkan program studi") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = nomorHP,
                onValueChange = {
                    nomorHP = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Nomor HP") },
                placeholder = { Text("Contoh: 08123456789") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                enabled = !uiState.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Email") },
                placeholder = { Text("Masukkan email valid") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = !isEmailValid,
                supportingText = {
                    if (!isEmailValid) {
                        Text(
                            text = "Format email tidak valid",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Password") },
                placeholder = { Text("Minimal 6 karakter") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !uiState.isLoading,
                supportingText = {
                    if (password.isNotEmpty() && password.length < 6) {
                        Text(
                            text = "Password minimal 6 karakter",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (uiState.errorMessage != null) {
                        authViewModel.clearError()
                    }
                },
                label = { Text("Konfirmasi Password") },
                placeholder = { Text("Masukkan ulang password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !uiState.isLoading,
                isError = !isPasswordMatch,
                supportingText = {
                    if (!isPasswordMatch) {
                        Text(
                            text = "Password tidak cocok",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Tombol Daftar
            val isFormValid = email.isNotEmpty() &&
                    password.isNotEmpty() &&
                    password.length >= 6 &&
                    isPasswordMatch &&
                    nama.isNotEmpty() &&
                    nim.isNotEmpty() &&
                    isNIMValid &&
                    prodi.isNotEmpty() &&
                    nomorHP.isNotEmpty() &&
                    isEmailValid

            Button(
                onClick = {
                    if (isFormValid) {
                        authViewModel.register(
                            email = email.trim(),
                            password = password,
                            nama = nama.trim(),
                            nim = nim.trim(),
                            prodi = prodi.trim(),
                            nomorHP = nomorHP.trim()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !uiState.isLoading && isFormValid,
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
                        Text("Mendaftar...")
                    }
                } else {
                    Text(
                        "DAFTAR",
                        fontWeight = FontWeight.Bold
                    )
                }
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
                            error.contains("email-already-in-use") -> "Email sudah terdaftar. Gunakan email lain."
                            error.contains("weak-password") -> "Password terlalu lemah. Gunakan kombinasi huruf dan angka."
                            error.contains("invalid-email") -> "Format email tidak valid."
                            error.contains("network") -> "Periksa koneksi internet Anda."
                            else -> "Pendaftaran gagal. Silakan coba lagi."
                        },
                        color = Color(0xFFD32F2F),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Link kembali ke login
            TextButton(
                onClick = onNavigateBack,
                enabled = !uiState.isLoading
            ) {
                Text(
                    "Sudah punya akun? Masuk",
                    color = Color(0xFF1E3A5F)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}