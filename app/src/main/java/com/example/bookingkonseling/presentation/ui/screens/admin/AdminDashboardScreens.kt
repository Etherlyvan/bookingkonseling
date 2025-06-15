// presentation/ui/screens/admin/AdminDashboardScreen.kt
package com.example.bookingkonseling.presentation.ui.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookingkonseling.R
import com.example.bookingkonseling.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color(0xFF1E3A5F)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "Admin Dashboard",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Kelola booking konseling mahasiswa",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats Cards
                item {
                    Text(
                        text = "Statistik Hari Ini",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            listOf(
                                StatCardData("Total Booking", uiState.stats.totalBookings.toString(), R.drawable.ic_schedule, Color(0xFF2196F3)),
                                StatCardData("Pending", uiState.stats.pendingBookings.toString(), R.drawable.ic_access_time, Color(0xFFFF9800)),
                                StatCardData("Ongoing", uiState.stats.ongoingBookings.toString(), Icons.Default.PlayArrow, Color(0xFF4CAF50)),
                                StatCardData("Completed", uiState.stats.completedBookings.toString(), Icons.Default.Check, Color(0xFF9C27B0))
                            )
                        ) { stat ->
                            StatisticCardXml(stat)
                        }
                    }
                }

                // Recent Bookings
                item {
                    Text(
                        text = "Booking Terbaru",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(uiState.bookings.take(5)) { booking ->
                    AdminBookingCard(
                        booking = booking,
                        onStatusChange = { bookingId, status ->
                            viewModel.updateBookingStatus(bookingId, status)
                        }
                    )
                }

                item {
                    Button(
                        onClick = { /* Navigate to full booking list */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E3A5F)
                        )
                    ) {
                        Text("Lihat Semua Booking")
                    }
                }
            }
        }

        // Error handling
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                println("Admin Dashboard Error: $error")
            }
        }
    }
}

// Data class untuk stat card yang mendukung both ImageVector dan drawable resource
data class StatCardData(
    val title: String,
    val value: String,
    val icon: Any, // Bisa ImageVector atau Int (drawable resource)
    val color: Color
)

@Composable
fun StatisticCardXml(stat: StatCardData) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = stat.color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon - support both ImageVector and drawable resource
            when (stat.icon) {
                is ImageVector -> {
                    Icon(
                        stat.icon,
                        contentDescription = null,
                        tint = stat.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                is Int -> {
                    Icon(
                        painter = painterResource(id = stat.icon),
                        contentDescription = null,
                        tint = stat.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column {
                Text(
                    text = stat.value,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = stat.color
                )
                Text(
                    text = stat.title,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun AdminBookingCard(
    booking: com.example.bookingkonseling.data.model.Booking,
    onStatusChange: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.namaMahasiswa,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NIM: ${booking.nimMahasiswa}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = booking.sesi,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Status Badge
                val statusColor = when (booking.status) {
                    "Pending" -> Color(0xFFFF9800)
                    "Ongoing" -> Color(0xFF4CAF50)
                    "Completed" -> Color(0xFF2196F3)
                    "Cancelled" -> Color(0xFFF44336)
                    else -> Color.Gray
                }

                Surface(
                    color = statusColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = booking.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            if (booking.status == "Pending") {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onStatusChange(booking.id, "Ongoing") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Terima", color = Color.White)
                    }

                    OutlinedButton(
                        onClick = { onStatusChange(booking.id, "Cancelled") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Tolak")
                    }
                }
            } else if (booking.status == "Ongoing") {
                Button(
                    onClick = { onStatusChange(booking.id, "Completed") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Selesaikan", color = Color.White)
                }
            }
        }
    }
}