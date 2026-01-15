package com.uansari.expenseflow

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.uansari.expenseflow.core.navigation.BottomNavItem
import com.uansari.expenseflow.core.navigation.NavGraph
import com.uansari.expenseflow.core.navigation.Routes
import com.uansari.expenseflow.core.ui.theme.ExpenseFlowTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseFlowTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Track current route for bottom nav highlighting
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Determine if we should show bottom nav and FAB
    // Only show on main screens, hide on detail screens
    val showBottomBar = currentRoute in listOf(
        Routes.Dashboard.route, Routes.Transactions.route, Routes.Settings.route
    )

    Scaffold(
        // ════════════════════════════════════════════════════════════
        // Bottom Navigation Bar
        // ════════════════════════════════════════════════════════════
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute, onItemClick = { item ->
                        navController.navigate(item.route) {
                            // Pop up to start destination to avoid
                            // building up a large back stack
                            popUpTo(navController.graph.startDestinationId) {

                                saveState = true
                            }
                            // Avoid multiple copies of same destination
                            launchSingleTop = true
                            // Restore state when reselecting
                            restoreState = true
                        }
                    })
            }
        },

        // ════════════════════════════════════════════════════════════
        // Floating Action Button
        // ════════════════════════════════════════════════════════════
        floatingActionButton = {
            if (showBottomBar) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Routes.AddTransaction.route)
                    }, containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Add, contentDescription = "Add Transaction"
                    )
                }
            }
        }

    ) { paddingValues ->
        NavGraph(
            navController = navController, modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun BottomNavBar(
    currentRoute: String?, onItemClick: (BottomNavItem) -> Unit, modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface
    ) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected, onClick = { onItemClick(item) }, icon = {
                Icon(
                    imageVector = if (isSelected) {
                        item.selectedIcon
                    } else {
                        item.icon
                    }, contentDescription = item.label
                )
            }, label = {
                Text(text = item.label)
            }, colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
            )
        }
    }
}