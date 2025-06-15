// presentation/viewmodel/AdminViewModel.kt
package com.example.bookingkonseling.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingkonseling.data.model.Booking
import com.example.bookingkonseling.data.model.BookingStats
import com.example.bookingkonseling.data.model.User
import com.example.bookingkonseling.data.repository.AdminRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AdminUiState(
    val isLoading: Boolean = false,
    val bookings: List<Booking> = emptyList(),
    val users: List<User> = emptyList(),
    val stats: BookingStats = BookingStats(),
    val errorMessage: String? = null,
    val selectedFilter: String = "All"
)

class AdminViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState: StateFlow<AdminUiState> = _uiState

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // Load stats
                repository.getBookingStats()
                    .onSuccess { stats ->
                        _uiState.value = _uiState.value.copy(stats = stats)
                    }

                // Load bookings
                loadBookings()

                // Load users
                repository.getAllUsers()
                    .onSuccess { users ->
                        _uiState.value = _uiState.value.copy(users = users)
                    }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = e.message ?: "Terjadi kesalahan"
                )
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    fun loadBookings(status: String? = null) {
        viewModelScope.launch {
            repository.getAllBookings(status)
                .onSuccess { bookings ->
                    _uiState.value = _uiState.value.copy(
                        bookings = bookings,
                        selectedFilter = status ?: "All"
                    )
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Gagal memuat booking"
                    )
                }
        }
    }

    fun updateBookingStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
                .onSuccess {
                    loadBookings(_uiState.value.selectedFilter)
                    loadDashboardData() // Refresh stats
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Gagal update status"
                    )
                }
        }
    }

    fun assignKonselor(bookingId: String, konselorName: String) {
        viewModelScope.launch {
            repository.assignKonselor(bookingId, konselorName)
                .onSuccess {
                    loadBookings(_uiState.value.selectedFilter)
                    loadDashboardData()
                }
                .onFailure { e ->
                    _uiState.value = _uiState.value.copy(
                        errorMessage = e.message ?: "Gagal assign konselor"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}