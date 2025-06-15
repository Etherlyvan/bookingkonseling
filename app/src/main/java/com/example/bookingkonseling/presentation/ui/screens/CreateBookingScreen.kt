// presentation/ui/screens/CreateBookingScreen.kt
package com.example.bookingkonseling.presentation.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bookingkonseling.R
import com.example.bookingkonseling.presentation.viewmodel.BookingViewModel
import com.google.firebase.Timestamp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateBookingScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val viewModel = remember { BookingViewModel(context) }

    // State untuk form
    var nama by remember { mutableStateOf("") }
    var nim by remember { mutableStateOf("") }
    var prodi by remember { mutableStateOf("") }
    var nomorHP by remember { mutableStateOf("") }
    var tanggalString by remember { mutableStateOf("") }
    var selectedSesi by remember { mutableStateOf("") }
    var ktmUri by remember { mutableStateOf<Uri?>(null) }

    // State untuk dropdown
    var expandedSesi by remember { mutableStateOf(false) }

    // State untuk upload status
    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf("") }

    val sesiOptions = listOf(
        "Sesi 1 (10.00 - 11.00)",
        "Sesi 2 (11.00 - 12.00)",
        "Sesi 3 (13.00 - 14.00)",
        "Sesi 4 (14.00 - 15.00)"
    )

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        ktmUri = uri
        if (uri != null) {
            uploadProgress = "File dipilih: ${uri.lastPathSegment}"
        }
    }

    // Observe UI state
    val uiState by viewModel.uiState.collectAsState()

    // Handle upload state
    LaunchedEffect(uiState.isLoading) {
        isUploading = uiState.isLoading
        if (uiState.isLoading) {
            uploadProgress = "Mengupload file dan menyimpan data..."
        }
    }

    // Jika berhasil create booking, kembali
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetSuccess()
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Buat Reservasi",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !isUploading
                    ) {
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
        containerColor = Color(0xFF1E3A5F)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Orange Warning Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF9800))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_info),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mohon isi data di bawah ini sesuai dengan KTM Anda",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Form Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E3A5F))
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Form Fields
                CustomTextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = "Nama Mahasiswa",
                    placeholder = "Masukkan nama lengkap",
                    enabled = !isUploading
                )

                CustomTextField(
                    value = nim,
                    onValueChange = { nim = it },
                    label = "NIM Mahasiswa",
                    placeholder = "Masukkan NIM",
                    enabled = !isUploading
                )

                CustomTextField(
                    value = prodi,
                    onValueChange = { prodi = it },
                    label = "Prodi Mahasiswa",
                    placeholder = "Masukkan nama prodi",
                    enabled = !isUploading
                )

                CustomTextField(
                    value = nomorHP,
                    onValueChange = { nomorHP = it },
                    label = "Nomor HP",
                    placeholder = "Masukkan nomor HP",
                    enabled = !isUploading
                )

                CustomTextField(
                    value = tanggalString,
                    onValueChange = { tanggalString = it },
                    label = "Pilih Tanggal",
                    placeholder = "Senin, 10/9/2025",
                    enabled = !isUploading
                )

                CustomDropdown(
                    value = selectedSesi,
                    onValueChange = { selectedSesi = it },
                    label = "Pilih Sesi",
                    options = sesiOptions,
                    expanded = expandedSesi,
                    onExpandedChange = { expandedSesi = it },
                    placeholder = "Sesi 1 (10.00 - 11.00)",
                    enabled = !isUploading
                )

                // Upload KTM Section
                Column {
                    Text(
                        text = "Upload KTM *",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedButton(
                        onClick = {
                            if (!isUploading) {
                                imagePickerLauncher.launch("image/*")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (ktmUri != null) Color(0xFF4CAF50) else Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.dp,
                            brush = androidx.compose.ui.graphics.SolidColor(
                                if (ktmUri != null) Color(0xFF4CAF50) else Color.White
                            )
                        ),
                        enabled = !isUploading
                    ) {
                        if (ktmUri != null) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_upload),
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            if (ktmUri != null) "✓ KTM Terpilih" else "Pilih File KTM",
                            color = if (ktmUri != null) Color(0xFF4CAF50) else Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Upload progress atau info file
                    if (uploadProgress.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = uploadProgress,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }

                    // File requirements info
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Format: JPG, PNG, GIF\n• Ukuran maksimal: 5MB\n• Pastikan foto KTM jelas dan terbaca",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Submit Button
                Button(
                    onClick = {
                        val currentDate = Date()
                        val timestamp = Timestamp(currentDate)

                        viewModel.createBooking(
                            nama = nama,
                            nim = nim,
                            prodi = prodi,
                            nomorHP = nomorHP,
                            tanggal = timestamp,
                            sesi = selectedSesi,
                            konselor = "Dr. Konselor",
                            ktmUri = ktmUri
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isUploading &&
                            nama.isNotEmpty() &&
                            nim.isNotEmpty() &&
                            prodi.isNotEmpty() &&
                            nomorHP.isNotEmpty() &&
                            tanggalString.isNotEmpty() &&
                            selectedSesi.isNotEmpty()
                ) {
                    if (isUploading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Menyimpan...",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        Text(
                            "Kirim Reservasi",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }

                // Error message
                uiState.errorMessage?.let { error ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0x20FF5722)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFAB91),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = error,
                                color = Color(0xFFFFAB91),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    placeholder,
                    color = Color(0xFF60A5FA),
                    fontSize = 16.sp
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                disabledTextColor = Color.White.copy(alpha = 0.6f),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.White,
                unfocusedIndicatorColor = Color(0xFF60A5FA),
                disabledIndicatorColor = Color(0xFF60A5FA).copy(alpha = 0.6f),
                cursorColor = Color.White
            ),
            singleLine = true,
            enabled = enabled
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    options: List<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    placeholder: String,
    enabled: Boolean = true
) {
    Column {
        Text(
            text = label,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box {
            OutlinedTextField(
                value = value.ifEmpty { placeholder },
                onValueChange = { },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = if (value.isEmpty()) Color(0xFF60A5FA) else Color.White,
                    unfocusedTextColor = if (value.isEmpty()) Color(0xFF60A5FA) else Color.White,
                    disabledTextColor = Color.White.copy(alpha = 0.6f),
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color(0xFF60A5FA),
                    disabledBorderColor = Color(0xFF60A5FA).copy(alpha = 0.6f),
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(
                        onClick = {
                            if (enabled) {
                                onExpandedChange(!expanded)
                            }
                        },
                        enabled = enabled
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = if (expanded) "Close dropdown" else "Open dropdown",
                            tint = if (enabled) Color.White else Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                enabled = enabled
            )

            DropdownMenu(
                expanded = expanded && enabled,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                option,
                                fontSize = 16.sp
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }
    }
}