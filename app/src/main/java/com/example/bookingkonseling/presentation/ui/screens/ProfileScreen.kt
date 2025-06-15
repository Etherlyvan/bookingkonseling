// presentation/ui/screens/ProfileScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookingkonseling.R
import com.example.bookingkonseling.data.repository.AuthRepository
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import androidx.compose.runtime.collectAsState

@Composable
fun ProfileScreen(
    viewModel: AuthViewModel = viewModel() // PERBAIKAN: Tambahkan viewModel parameter
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authRepository = AuthRepository()
    val scope = rememberCoroutineScope()

    // PERBAIKAN: Observe UI state dari AuthViewModel
    val uiState by viewModel.uiState.collectAsState()

    var userData by remember { mutableStateOf<com.example.bookingkonseling.data.model.User?>(null) }

    // Load user data
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            scope.launch {
                authRepository.getUserData(user.uid)
                    .onSuccess { data ->
                        userData = data
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // Header Profile
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Color(0xFF1E3A5F),
                    RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Image
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_person),
                        contentDescription = "Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        tint = Color(0xFF1E3A5F)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = userData?.nama ?: "Loading...",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = userData?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Info Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProfileInfoCard(
                icon = Icons.Default.Star,
                label = "NIM",
                value = userData?.nim ?: "-"
            )

            ProfileInfoCard(
                icon = Icons.Default.Home,
                label = "Program Studi",
                value = userData?.prodi ?: "-"
            )

            ProfileInfoCard(
                icon = Icons.Default.Phone,
                label = "Nomor HP",
                value = userData?.nomorHP ?: "-"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // PERBAIKAN: Logout Button dengan proper state handling
        Button(
            onClick = {
                try {
                    viewModel.logout()
                } catch (e: Exception) {
                    println("Logout button error: ${e.message}")
                    viewModel.logout()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF6B6B)
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoggingOut // PERBAIKAN: Gunakan uiState yang sudah di-observe
        ) {
            if (uiState.isLoggingOut) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Icon(Icons.Default.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "Logout",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // PERBAIKAN: Show error message if any
        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ProfileInfoCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFE8E0FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color(0xFF1E3A5F),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}