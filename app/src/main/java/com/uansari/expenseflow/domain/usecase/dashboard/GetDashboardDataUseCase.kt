package com.uansari.expenseflow.domain.usecase.dashboard

import com.uansari.expenseflow.domain.model.CategoryWithAmount
import com.uansari.expenseflow.domain.model.DashboardData
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.AccountRepository
import com.uansari.expenseflow.domain.repository.CategoryRepository
import com.uansari.expenseflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.util.Calendar
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val accountRepository: AccountRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository
) {

    operator fun invoke(): Flow<DashboardData> {

        // Calculate current month's date range
        val (startOfMonth, endOfMonth) = getCurrentMonthRange()

        // Combine multiple data streams
        return combine(
            accountRepository.getTotalBalance(),
            transactionRepository.getTotalByTypeAndDateRange(
                type = TransactionType.INCOME, startDate = startOfMonth, endDate = endOfMonth
            ),
            transactionRepository.getTotalByTypeAndDateRange(
                type = TransactionType.EXPENSE, startDate = startOfMonth, endDate = endOfMonth
            ),
            transactionRepository.getRecentTransactions(limit = 5),
            getSpendingByCategoryFlow(startOfMonth, endOfMonth)
        ) { totalBalance, monthlyIncome, monthlyExpense, recentTransactions, spendingByCategory ->

            DashboardData(
                totalBalance = totalBalance,
                monthlyIncome = monthlyIncome,
                monthlyExpense = monthlyExpense,
                recentTransactions = recentTransactions,
                spendingByCategory = spendingByCategory
            )
        }
    }

    // ════════════════════════════════════════════════════════════════
    // Helper: Get current month's start and end timestamps
    // ════════════════════════════════════════════════════════════════

    private fun getCurrentMonthRange(): Pair<Long, Long> {
        val calendar = Calendar.getInstance()

        // Start of month (day 1, 00:00:00.000)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        // End of month (last day, 23:59:59.999)
        calendar.set(
            Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfMonth = calendar.timeInMillis

        return startOfMonth to endOfMonth
    }

    // ════════════════════════════════════════════════════════════════
    // Helper: Get spending by category with full Category objects
    // ════════════════════════════════════════════════════════════════

    private fun getSpendingByCategoryFlow(
        startDate: Long, endDate: Long
    ): Flow<List<CategoryWithAmount>> {

        return combine(
            transactionRepository.getSpendingByCategory(
                startDate,
                endDate
            ), categoryRepository.getCategoriesByType(TransactionType.EXPENSE)
        ) { spendingMap, categories ->

            // spendingMap: Map<categoryId, totalAmount>
            // categories: List<Category>

            // Calculate total expense for percentages
            val totalExpense = spendingMap.values.sum()

            // Map category IDs to full Category objects
            val categoryMap = categories.associateBy { it.id }

            // Create CategoryWithAmount list
            spendingMap.mapNotNull { (categoryId, amount) ->
                val category = categoryMap[categoryId] ?: return@mapNotNull null

                val percentage = if (totalExpense > 0) {
                    ((amount / totalExpense) * 100).toFloat()
                } else {
                    0f
                }

                CategoryWithAmount(
                    category = category, amount = amount, percentage = percentage
                )
            }.sortedByDescending { it.amount }  // Highest first
        }
    }
}