// presentation/ui/screens/BookingDetailScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookingkonseling.data.model.Booking

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    booking: Booking,
    onNavigateBack: () -> Unit,
    onCancel: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Booking",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E3A5F)
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Status Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sedang Berlangsung",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    // Title
                    Text(
                        text = "Pump It Up",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Sesi 2 (08.00 - 10.00 WIB)",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Placeholder Image
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(
                                Color(0xFFE0E0E0),
                                RoundedCornerShape(8.dp)
                            )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detail Information
                    DetailInfoRow("Nama", booking.namaMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("NIM", booking.nimMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Program Studi", booking.prodiMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Nomor Telepon", booking.nomorHP)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Cancel Button
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE57373)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    "Batalkan",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DetailInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = ": $value",
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}