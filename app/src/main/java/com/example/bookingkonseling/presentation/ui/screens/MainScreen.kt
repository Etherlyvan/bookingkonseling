// presentation/ui/screens/MainScreen.kt

package com.example.bookingkonseling.presentation.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.bookingkonseling.R
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel
import com.example.bookingkonseling.data.model.Booking
import com.google.gson.Gson
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    authViewModel: AuthViewModel
) {
    val navController = rememberNavController()

    // PERBAIKAN: State untuk menyimpan booking yang dipilih
    var selectedBooking by remember { mutableStateOf<Booking?>(null) }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF1E3A5F)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    BottomNavItem(
                        route = "home",
                        icon = Icons.Default.Home,
                        label = "Beranda",
                        isVectorIcon = true
                    ),
                    BottomNavItem(
                        route = "history",
                        icon = null,
                        label = "Riwayat",
                        isVectorIcon = false,
                        drawableRes = R.drawable.ic_history
                    ),
                    BottomNavItem(
                        route = "profile",
                        icon = Icons.Default.Person,
                        label = "Profil",
                        isVectorIcon = true
                    )
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item.isVectorIcon && item.icon != null) {
                                Icon(item.icon, contentDescription = item.label)
                            } else if (item.drawableRes != null) {
                                Icon(
                                    painter = painterResource(id = item.drawableRes),
                                    contentDescription = item.label
                                )
                            }
                        },
                        label = { Text(item.label) },
                        selected = currentDestination?.hierarchy?.any {
                            it.route == item.route
                        } == true,
                        onClick = {
                            try {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            } catch (e: Exception) {
                                println("Navigation error: ${e.message}")
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF1E3A5F),
                            selectedTextColor = Color(0xFF1E3A5F),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0xFFE8E0FF)
                        )
                    )
                }
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(
                    onNavigateToBooking = {
                        navController.navigate("create_booking")
                    },
                    onNavigateToHistory = {
                        navController.navigate("history") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable("history") {
                BookingHistoryScreen(
                    onNavigateToDetail = { booking ->
                        // PERBAIKAN: Simpan booking di state dan navigate ke detail
                        selectedBooking = booking
                        navController.navigate("booking_detail")
                    }
                )
            }

            composable("profile") {
                ProfileScreen(viewModel = authViewModel)
            }

            composable("create_booking") {
                CreateBookingScreen(
                    onNavigateBack = {
                        navController.popBackStack()
                    }
                )
            }

            // PERBAIKAN: Route booking detail tanpa parameter
            composable("booking_detail") {
                selectedBooking?.let { booking ->
                    BookingDetailScreen(
                        booking = booking,
                        onNavigateBack = {
                            selectedBooking = null // Clear state
                            navController.popBackStack()
                        },
                        onCancel = {
                            selectedBooking = null // Clear state
                            navController.popBackStack()
                        }
                    )
                } ?: run {
                    // PERBAIKAN: Handle jika booking null, kembali ke history
                    LaunchedEffect(Unit) {
                        navController.popBackStack()
                    }
                }
            }
        }
    }
}

data class BottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val label: String,
    val isVectorIcon: Boolean = true,
    val drawableRes: Int? = null
)