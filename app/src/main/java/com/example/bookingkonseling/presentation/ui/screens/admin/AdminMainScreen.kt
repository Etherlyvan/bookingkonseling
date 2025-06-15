// presentation/ui/screens/admin/AdminMainScreen.kt
package com.example.bookingkonseling.presentation.ui.screens.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.bookingkonseling.R
import com.example.bookingkonseling.presentation.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMainScreen(
    authViewModel: AuthViewModel // PERBAIKAN: Tambah parameter authViewModel
) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                contentColor = Color(0xFF1E3A5F)
            ) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    AdminBottomNavItem(
                        route = "admin_dashboard",
                        icon = null,
                        label = "Dashboard",
                        drawableRes = R.drawable.ic_dashboard
                    ),
                    AdminBottomNavItem(
                        route = "admin_bookings",
                        icon = Icons.Default.List,
                        label = "Booking"
                    ),
                    AdminBottomNavItem(
                        route = "admin_users",
                        icon = null,
                        label = "Mahasiswa",
                        drawableRes = R.drawable.ic_people_simple
                    ),
                    AdminBottomNavItem(
                        route = "admin_profile",
                        icon = Icons.Default.Person,
                        label = "Profil"
                    )
                )

                items.forEach { item ->
                    NavigationBarItem(
                        icon = {
                            if (item.icon != null) {
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
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
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
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "admin_dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("admin_dashboard") {
                AdminDashboardScreen()
            }

            composable("admin_bookings") {
                AdminBookingListScreen()
            }

            composable("admin_users") {
                AdminUserListScreen()
            }

            composable("admin_profile") {
                AdminProfileScreen(viewModel = authViewModel) // PERBAIKAN: Pass authViewModel
            }
        }
    }
}

data class AdminBottomNavItem(
    val route: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val label: String,
    val drawableRes: Int? = null
)