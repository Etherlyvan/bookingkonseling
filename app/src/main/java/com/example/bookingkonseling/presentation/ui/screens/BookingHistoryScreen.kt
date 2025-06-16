// presentation/ui/screens/BookingHistoryScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.presentation.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryScreen(
    onNavigateToDetail: (Booking) -> Unit = {}
) {
    val context = LocalContext.current
    val viewModel = remember { BookingViewModel(context) }
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Riwayat Booking",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A5F)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF1E3A5F))
            }
        } else if (uiState.bookings.isEmpty()) {
            EmptyBookingState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.bookings) { booking ->
                    BookingHistoryCard(
                        booking = booking,
                        onCancel = {
                            viewModel.cancelBooking(booking.id)
                        },
                        onClick = {
                            // PERBAIKAN: Pastikan onClick dipanggil dengan benar
                            onNavigateToDetail(booking)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun BookingHistoryCard(
    booking: Booking,
    onCancel: () -> Unit,
    onClick: () -> Unit = {}
) {
    val formattedDate = try {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        dateFormat.format(booking.tanggal.toDate())
    } catch (e: Exception) {
        "10 September 2024"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // PERBAIKAN: Pastikan onClick dipanggil
                onClick()
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE8D5FF)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Konseling",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
                Text(
                    text = formattedDate,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Konselor Info
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF7B2CBF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = booking.konselor.ifEmpty { "Belum ditentukan" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = booking.sesi,
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    // PERBAIKAN: Tambahkan status
                    Text(
                        text = "Status: ${booking.status}",
                        fontSize = 12.sp,
                        color = when(booking.status) {
                            "Pending" -> Color(0xFFFF9800)
                            "Ongoing" -> Color(0xFF4CAF50)
                            "Completed" -> Color(0xFF2196F3)
                            "Cancelled" -> Color(0xFFF44336)
                            else -> Color.Gray
                        },
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Cancel Button - hanya tampil jika status bisa dibatalkan
                if (booking.status == "Pending" || booking.status == "Ongoing") {
                    Button(
                        onClick = {
                            // PERBAIKAN: Prevent event bubbling
                            onCancel()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFE57373)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            "Batalkan",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyBookingState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📋",
                fontSize = 48.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Belum ada data reservasi tersimpan.",
            fontSize = 16.sp,
            color = Color(0xFF666666),
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "Buat reservasi sekarang",
            fontSize = 14.sp,
            color = Color(0xFF666666)
        )
    }
}