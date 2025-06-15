// presentation/viewmodel/BookingViewModel.kt
package com.example.bookingkonseling.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.data.repository.BookingRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookingUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class BookingViewModel(private val context: Context) : ViewModel() {
    private val repository = BookingRepository(context)
    private val currentUser = FirebaseAuth.getInstance().currentUser

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState

    init {
        loadUserBookings()
    }

    fun loadUserBookings() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                currentUser?.let { user ->
                    repository.getUserBookings(user.uid)
                        .onSuccess { bookings ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                bookings = bookings,
                                errorMessage = null
                            )
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Terjadi kesalahan"
                            )
                        }
                } ?: run {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "User tidak ditemukan"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun createBooking(
        nama: String,
        nim: String,
        prodi: String,
        nomorHP: String,
        tanggal: Timestamp,
        sesi: String,
        konselor: String,
        ktmUri: Uri?
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                currentUser?.let { user ->
                    val booking = Booking(
                        userId = user.uid,
                        namaMahasiswa = nama,
                        nimMahasiswa = nim,
                        prodiMahasiswa = prodi,
                        nomorHP = nomorHP,
                        tanggal = tanggal,
                        sesi = sesi,
                        konselor = konselor
                    )

                    repository.createBooking(booking, ktmUri)
                        .onSuccess {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                            loadUserBookings()
                        }
                        .onFailure { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                errorMessage = exception.message ?: "Gagal membuat booking"
                            )
                        }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun cancelBooking(bookingId: String) {
        viewModelScope.launch {
            try {
                repository.updateBookingStatus(bookingId, "Cancelled")
                    .onSuccess {
                        loadUserBookings()
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = exception.message ?: "Gagal membatalkan booking"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Terjadi kesalahan tidak terduga"
                )
            }
        }
    }

    fun resetSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}