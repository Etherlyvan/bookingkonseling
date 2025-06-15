// presentation/viewmodel/AuthViewModel.kt
package com.example.bookingkonseling.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookingkonseling.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

// State untuk UI Authentication
data class AuthUiState(
    val isLoading: Boolean = false,
    val isLoggedIn: Boolean = false,
    val errorMessage: String? = null,
    val isLoggingOut: Boolean = false // TAMBAHAN: State untuk logout
)

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    // State untuk UI
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    init {
        // Cek apakah user sudah login
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        try {
            val currentUser = repository.getCurrentUser()
            _uiState.value = _uiState.value.copy(
                isLoggedIn = currentUser != null,
                isLoading = false
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoggedIn = false,
                isLoading = false,
                errorMessage = "Error checking auth status: ${e.message}"
            )
        }
    }

    // Fungsi login
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                repository.login(email, password)
                    .onSuccess {
                        _uiState.value = AuthUiState(isLoggedIn = true)
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Login gagal"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    // Fungsi register
    fun register(
        email: String,
        password: String,
        nama: String,
        nim: String,
        prodi: String,
        nomorHP: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

                repository.register(email, password, nama, nim, prodi, nomorHP)
                    .onSuccess {
                        _uiState.value = AuthUiState(isLoggedIn = true)
                    }
                    .onFailure { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Register gagal"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    // PERBAIKAN: Fungsi logout yang lebih robust
    fun logout() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoggingOut = true,
                    errorMessage = null
                )

                // Delay kecil untuk memastikan UI update
                delay(100)

                // Logout dari Firebase
                repository.logout()

                // Delay untuk memastikan logout berhasil
                delay(200)

                // Update state
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    isLoggingOut = false
                )

            } catch (e: Exception) {
                // Tetap logout meskipun ada error
                _uiState.value = AuthUiState(
                    isLoggedIn = false,
                    isLoggingOut = false,
                    errorMessage = "Logout completed with warning: ${e.message}"
                )
            }
        }
    }

    // Fungsi untuk clear error
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}