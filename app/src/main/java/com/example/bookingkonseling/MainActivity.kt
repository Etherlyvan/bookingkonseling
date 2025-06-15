// MainActivity.kt
package com.example.bookingkonseling

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.bookingkonseling.navigation.Navigation
import com.example.bookingkonseling.navigation.Screen
import com.example.bookingkonseling.presentation.ui.theme.BookingKonselingTheme
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BookingKonselingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BookingKonselingApp()
                }
            }
        }
    }
}

@Composable
fun BookingKonselingApp() {
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    // PERBAIKAN: Determine start destination berdasarkan auth status
    val startDestination = remember(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            // Jika sudah login, cek role user
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser?.email?.contains("admin") == true ||
                currentUser?.email?.endsWith("@admin.ub.ac.id") == true) {
                Screen.AdminMain.route
            } else {
                Screen.Main.route
            }
        } else {
            // PERBAIKAN: Jika belum login, selalu ke login page
            Screen.Login.route
        }
    }

    // PERBAIKAN: Handle auth state changes untuk auto navigation
    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            // User logged out atau belum login, navigate ke login
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true } // Clear entire back stack
            }
        }
    }

    Navigation(
        navController = navController,
        startDestination = startDestination,
        authViewModel = authViewModel
    )
}