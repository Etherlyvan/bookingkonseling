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

    // PERBAIKAN: Handle auth state changes untuk auto navigation
    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            // User logged out, navigate to login
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true } // Clear entire back stack
            }
        }
    }

    // Determine start destination based on current auth state
    val startDestination = remember(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            // Check if user is admin
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser?.email?.contains("admin") == true ||
                currentUser?.email?.endsWith("@admin.ub.ac.id") == true) {
                Screen.AdminMain.route
            } else {
                Screen.Main.route
            }
        } else {
            Screen.Login.route
        }
    }

    // PERBAIKAN: Pass navController dan authViewModel ke Navigation
    Navigation(
        navController = navController,
        startDestination = startDestination,
        authViewModel = authViewModel
    )
}