// navigation/Navigation.kt
package com.example.bookingkonseling.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookingkonseling.presentation.ui.screens.*
import com.example.bookingkonseling.presentation.ui.screens.admin.AdminMainScreen
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route,
    authViewModel: AuthViewModel
) {
    var userRole by remember { mutableStateOf<String?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authState by authViewModel.uiState.collectAsState()

    // PERBAIKAN: Tampilkan loading saat initial check
    if (!authState.isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFF1E3A5F)
            )
        }
        return
    }

    // Check user role
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            launch {
                try {
                    userRole = if (user.email?.contains("admin") == true ||
                        user.email?.endsWith("@admin.ub.ac.id") == true) {
                        "admin"
                    } else {
                        "student"
                    }
                } catch (e: Exception) {
                    println("Error checking user role: ${e.message}")
                    userRole = "student"
                }
            }
        }
    }

    // PERBAIKAN: Auto navigate berdasarkan auth state
    LaunchedEffect(authState.isLoggedIn) {
        if (authState.isLoggedIn) {
            // User logged in, navigate to appropriate screen
            val destination = if (userRole == "admin") {
                Screen.AdminMain.route
            } else {
                Screen.Main.route
            }

            if (navController.currentDestination?.route != destination) {
                navController.navigate(destination) {
                    popUpTo(0) { inclusive = true }
                }
            }
        } else if (authState.isInitialized) {
            // User not logged in and initialization complete, ensure on login screen
            if (navController.currentDestination?.route != Screen.Login.route &&
                navController.currentDestination?.route != Screen.Register.route) {
                navController.navigate(Screen.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route // PERBAIKAN: Selalu mulai dari login
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    // Navigation akan ditangani oleh LaunchedEffect di atas
                },
                authViewModel = authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    // Navigation akan ditangani oleh LaunchedEffect di atas
                },
                authViewModel = authViewModel
            )
        }

        // Student Main Screen
        composable(Screen.Main.route) {
            // PERBAIKAN: Hanya tampilkan jika user sudah login
            if (authState.isLoggedIn) {
                MainScreen(authViewModel = authViewModel)
            }
        }

        // Admin Main Screen
        composable(Screen.AdminMain.route) {
            // PERBAIKAN: Hanya tampilkan jika user sudah login sebagai admin
            if (authState.isLoggedIn) {
                AdminMainScreen(authViewModel = authViewModel)
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object AdminMain : Screen("admin_main")
}