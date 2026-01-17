package com.uansari.expenseflow.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.uansari.expenseflow.feature.accounts.AccountScreen
import com.uansari.expenseflow.feature.categories.CategoryScreen
import com.uansari.expenseflow.feature.dashboard.DashboardScreen
import com.uansari.expenseflow.feature.settings.SettingsScreen
import com.uansari.expenseflow.feature.transaction_management.TransactionManagementScreen
import com.uansari.expenseflow.feature.transactions.TransactionsScreen

@Composable
fun NavGraph(
    navController: NavHostController, modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard.route,
        modifier = modifier
    ) {

        // ════════════════════════════════════════════════════════════
        // Bottom Navigation Destinations
        // ════════════════════════════════════════════════════════════

        composable(route = Routes.Dashboard.route) {
            DashboardScreen(onNavigateToAddTransaction = {
                navController.navigate(Routes.AddTransaction.route)
            }, onNavigateToAllTransactions = {
                navController.navigate(Routes.Transactions.route)
            }, onNavigateToTransactionDetail = { transactionId ->
                navController.navigate(
                    Routes.createEditTransactionRoute(transactionId)
                )
            })
        }

        composable(route = Routes.Transactions.route) {
            TransactionsScreen(onNavigateToAddTransaction = {
                navController.navigate(Routes.AddTransaction.route)
            }, onNavigateToEditTransaction = { transactionId ->
                navController.navigate(
                    Routes.createEditTransactionRoute(transactionId)
                )
            })
        }

        composable(route = Routes.Settings.route) {
            SettingsScreen(onNavigateToCategories = {
                navController.navigate(Routes.Categories.route)
            }, onNavigateToAccounts = {
                navController.navigate(Routes.Accounts.route)
            })
        }

        // ════════════════════════════════════════════════════════════
        // Transaction Screens
        // ════════════════════════════════════════════════════════════

        composable(route = Routes.AddTransaction.route) {
            TransactionManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                })
        }

        composable(
            route = Routes.EditTransaction.route, arguments = listOf(
                navArgument("transactionId") {
                    type = NavType.LongType
                })
        ) { backStackEntry ->
            backStackEntry.arguments?.getLong("transactionId")

            TransactionManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                })
        }

        // ════════════════════════════════════════════════════════════
        // Management Screens
        // ════════════════════════════════════════════════════════════

        composable(route = Routes.Categories.route) {
            CategoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                })
        }

        composable(route = Routes.Accounts.route) {
            AccountScreen(
                onNavigateBack = {
                    navController.popBackStack()
                })
        }
    }
}