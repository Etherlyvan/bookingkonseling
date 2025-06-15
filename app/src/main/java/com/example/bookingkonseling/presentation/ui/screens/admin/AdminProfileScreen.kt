// presentation/ui/screens/admin/AdminProfileScreen.kt
package com.example.bookingkonseling.presentation.ui.screens.admin

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
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminProfileScreen(
    viewModel: AuthViewModel = viewModel() // PERBAIKAN: Tambahkan viewModel parameter
) {
    val currentUser = FirebaseAuth.getInstance().currentUser

    // PERBAIKAN: Observe UI state dari AuthViewModel
    val uiState by viewModel.uiState.collectAsState()

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
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_admin_panel),
                        contentDescription = "Admin",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF1E3A5F)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Administrator",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = currentUser?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Admin Menu Items
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdminMenuCard(
                iconRes = R.drawable.ic_schedule,
                title = "Jadwal Konselor",
                subtitle = "Atur jadwal konselor"
            )

            AdminMenuCard(
                iconRes = R.drawable.ic_assessment,
                title = "Laporan",
                subtitle = "Lihat laporan dan statistik"
            )

            AdminMenuCard(
                iconRes = R.drawable.ic_backup,
                title = "Backup Data",
                subtitle = "Backup dan restore data"
            )

            AdminMenuCard(
                icon = Icons.Default.Settings,
                title = "Pengaturan Sistem",
                subtitle = "Kelola pengaturan aplikasi"
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // PERBAIKAN: Logout Button dengan proper state handling
        Button(
            onClick = {
                try {
                    viewModel.logout()
                } catch (e: Exception) {
                    println("Admin logout error: ${e.message}")
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
fun AdminMenuCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    iconRes: Int? = null,
    title: String,
    subtitle: String
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
                if (icon != null) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = Color(0xFF1E3A5F),
                        modifier = Modifier.size(24.dp)
                    )
                } else if (iconRes != null) {
                    Icon(
                        painter = painterResource(id = iconRes),
                        contentDescription = null,
                        tint = Color(0xFF1E3A5F),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Icon(
                Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}