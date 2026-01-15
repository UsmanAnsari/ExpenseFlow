package com.uansari.expenseflow.domain.model

data class DashboardData(
    val totalBalance: Double = 0.0,

    val monthlyIncome: Double = 0.0,

    val monthlyExpense: Double = 0.0,

    val recentTransactions: List<TransactionDom> = emptyList(),

    val spendingByCategory: List<CategoryWithAmount> = emptyList()
)

data class CategoryWithAmount(
    val category: CategoryDom, val amount: Double, val percentage: Float = 0f
)