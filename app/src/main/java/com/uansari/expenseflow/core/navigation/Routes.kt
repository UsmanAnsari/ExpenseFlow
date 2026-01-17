package com.uansari.expenseflow.core.navigation

sealed class Routes(val route: String) {

    data object Dashboard : Routes("dashboard")

    data object Transactions : Routes("transactions")

    data object Settings : Routes("settings")

    data object AddTransaction : Routes("transaction/add")

    data object EditTransaction : Routes("transaction/edit/{transactionId}")


    data object Categories : Routes("categories")

    data object Accounts : Routes("accounts")

    companion object {
        fun createEditTransactionRoute(transactionId: Long): String {
            return "transaction/edit/$transactionId"
        }
    }
}