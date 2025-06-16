// presentation/ui/screens/BookingDetailScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.bookingkonseling.R
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.presentation.viewmodel.BookingViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    booking: Booking,
    onNavigateBack: () -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { BookingViewModel(context) }
    var showImageDialog by remember { mutableStateOf(false) }

    // Format tanggal
    val formattedDate = try {
        val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
        dateFormat.format(booking.tanggal.toDate())
    } catch (e: Exception) {
        "10 September 2024"
    }

    // Status color
    val statusColor = when (booking.status) {
        "Pending" -> Color(0xFFFF9800)
        "Ongoing" -> Color(0xFF4CAF50)
        "Completed" -> Color(0xFF2196F3)
        "Cancelled" -> Color(0xFFF44336)
        else -> Color.Gray
    }

    val statusText = when (booking.status) {
        "Pending" -> "Menunggu Konfirmasi"
        "Ongoing" -> "Sedang Berlangsung"
        "Completed" -> "Selesai"
        "Cancelled" -> "Dibatalkan"
        else -> booking.status
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Detail Booking",
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
                .verticalScroll(rememberScrollState())
        ) {
            // Status Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = statusText,
                    fontSize = 16.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Content Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
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
                        text = "Konseling Mahasiswa",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${booking.sesi} - $formattedDate",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // KTM Image Section
                    KTMImageSection(
                        ktmUrl = booking.ktmUrl,
                        onImageClick = { showImageDialog = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Detail Information
                    DetailInfoRow("Nama Mahasiswa", booking.namaMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("NIM", booking.nimMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Program Studi", booking.prodiMahasiswa)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Nomor Telepon", booking.nomorHP)
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Konselor", booking.konselor.ifEmpty { "Belum ditentukan" })
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailInfoRow("Status", booking.status)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons
            if (booking.status == "Pending" || booking.status == "Ongoing") {
                Button(
                    onClick = {
                        viewModel.cancelBooking(booking.id)
                        onCancel()
                    },
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
                        "Batalkan Booking",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }

    // Full Screen Image Dialog
    if (showImageDialog && booking.ktmUrl.isNotEmpty()) {
        FullScreenImageDialog(
            imageUrl = booking.ktmUrl,
            onDismiss = { showImageDialog = false }
        )
    }
}

@Composable
fun KTMImageSection(
    ktmUrl: String,
    onImageClick: () -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    if (ktmUrl.isNotEmpty()) {
        Text(
            text = "Foto KTM",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(ktmUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Foto KTM",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onImageClick() },
                    contentScale = ContentScale.Crop,
                    onLoading = { isLoading = true },
                    onSuccess = {
                        isLoading = false
                        hasError = false
                    },
                    onError = {
                        isLoading = false
                        hasError = true
                        println("Error loading KTM image: $ktmUrl")
                    }
                )

                // Loading indicator
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Error state
                if (hasError) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("❌", fontSize = 32.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Gagal memuat gambar",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Zoom button (hanya tampil jika gambar berhasil dimuat)
                if (!isLoading && !hasError) {
                    IconButton(
                        onClick = onImageClick,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                Color.Black.copy(alpha = 0.5f),
                                RoundedCornerShape(20.dp)
                            )
                            .size(40.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_zoom_in),
                            contentDescription = "Zoom",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Placeholder jika tidak ada KTM
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Color(0xFFE0E0E0),
                    RoundedCornerShape(8.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📋",
                    fontSize = 48.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tidak ada foto KTM",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun FullScreenImageDialog(
    imageUrl: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            // Image
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Foto KTM - Full Size",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            // Close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .background(
                        Color.Black.copy(alpha = 0.7f),
                        RoundedCornerShape(20.dp)
                    )
                    .size(48.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Info text
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Tap untuk menutup atau gunakan tombol X",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(12.dp)
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
            text = "$label:",
            fontSize = 14.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1.5f)
        )
    }
}