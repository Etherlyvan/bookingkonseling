// presentation/ui/screens/admin/AdminBookingListScreen.kt
package com.example.bookingkonseling.presentation.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookingkonseling.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminBookingListScreen(
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
            Text(
                text = "Kelola Booking",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                listOf("All", "Pending", "Ongoing", "Completed", "Cancelled")
            ) { status ->
                FilterChip(
                    onClick = { viewModel.loadBookings(if (status == "All") null else status) },
                    label = { Text(status) },
                    selected = uiState.selectedFilter == status,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFF1E3A5F),
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        // Booking List
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.bookings.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tidak ada booking",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    Text(
                        text = "untuk filter: ${uiState.selectedFilter}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.bookings) { booking ->
                    AdminBookingDetailCard(
                        booking = booking,
                        onStatusChange = { bookingId, status ->
                            viewModel.updateBookingStatus(bookingId, status)
                        },
                        onAssignKonselor = { bookingId, konselor ->
                            viewModel.assignKonselor(bookingId, konselor)
                        }
                    )
                }
            }
        }

        // Error handling
        uiState.errorMessage?.let { error ->
            LaunchedEffect(error) {
                println("Admin Booking List Error: $error")
            }
        }
    }
}

@Composable
fun AdminBookingDetailCard(
    booking: com.example.bookingkonseling.data.model.Booking,
    onStatusChange: (String, String) -> Unit,
    onAssignKonselor: (String, String) -> Unit
) {
    var showKonselorDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = booking.namaMahasiswa,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "NIM: ${booking.nimMahasiswa}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Text(
                        text = "Prodi: ${booking.prodiMahasiswa}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }

                // Status badge
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
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = statusColor,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detail info
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DetailRow("Tanggal", java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale("id", "ID")).format(booking.tanggal.toDate()))
                DetailRow("Sesi", booking.sesi)
                DetailRow("Konselor", booking.konselor)
                DetailRow("No. HP", booking.nomorHP)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            when (booking.status) {
                "Pending" -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showKonselorDialog = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            ),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Assign Konselor", color = Color.White)
                        }

                        OutlinedButton(
                            onClick = { onStatusChange(booking.id, "Cancelled") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Tolak")
                        }
                    }
                }
                "Ongoing" -> {
                    Button(
                        onClick = { onStatusChange(booking.id, "Completed") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Selesaikan Konseling", color = Color.White)
                    }
                }
            }
        }
    }

    // Konselor Assignment Dialog
    if (showKonselorDialog) {
        KonselorAssignmentDialog(
            onDismiss = { showKonselorDialog = false },
            onAssign = { konselor ->
                onAssignKonselor(booking.id, konselor)
                showKonselorDialog = false
            }
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun KonselorAssignmentDialog(
    onDismiss: () -> Unit,
    onAssign: (String) -> Unit
) {
    val konselorList = listOf(
        "Dr. Ahmad Konselor",
        "Dr. Siti Konselor",
        "Dr. Budi Konselor",
        "Dr. Maya Konselor"
    )

    var selectedKonselor by remember { mutableStateOf(konselorList.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Konselor") },
        text = {
            Column {
                Text("Pilih konselor untuk menangani booking ini:")
                Spacer(modifier = Modifier.height(16.dp))

                konselorList.forEach { konselor ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        RadioButton(
                            selected = selectedKonselor == konselor,
                            onClick = { selectedKonselor = konselor }
                        )
                        Text(
                            text = konselor,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAssign(selectedKonselor) }
            ) {
                Text("Assign")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}