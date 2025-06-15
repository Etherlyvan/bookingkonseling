// navigation/Navigation.kt
package com.example.bookingkonseling.navigation

import androidx.compose.runtime.*
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
    authViewModel: AuthViewModel // PERBAIKAN: Tambah parameter authViewModel
) {
    var userRole by remember { mutableStateOf<String?>(null) }
    val currentUser = FirebaseAuth.getInstance().currentUser
    val authState by authViewModel.uiState.collectAsState()

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

    // PERBAIKAN: Auto navigate to login when logged out
    LaunchedEffect(authState.isLoggedIn) {
        if (!authState.isLoggedIn && !authState.isLoading) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    try {
                        val destination = if (userRole == "admin") {
                            Screen.AdminMain.route
                        } else {
                            Screen.Main.route
                        }
                        navController.navigate(destination) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        println("Navigation error after login: ${e.message}")
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                authViewModel = authViewModel // PERBAIKAN: Pass authViewModel
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    try {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(Screen.Register.route) { inclusive = true }
                        }
                    } catch (e: Exception) {
                        println("Navigation error after register: ${e.message}")
                    }
                },
                authViewModel = authViewModel // PERBAIKAN: Pass authViewModel
            )
        }

        // Student Main Screen
        composable(Screen.Main.route) {
            MainScreen(authViewModel = authViewModel) // PERBAIKAN: Pass authViewModel
        }

        // Admin Main Screen
        composable(Screen.AdminMain.route) {
            AdminMainScreen(authViewModel = authViewModel) // PERBAIKAN: Pass authViewModel
        }
    }
}

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Main : Screen("main")
    object AdminMain : Screen("admin_main")
}