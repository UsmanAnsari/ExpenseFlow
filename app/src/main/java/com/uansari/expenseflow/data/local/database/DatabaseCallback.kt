package com.uansari.expenseflow.data.local.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.uansari.expenseflow.data.local.dao.AccountDao
import com.uansari.expenseflow.data.local.dao.CategoryDao
import com.uansari.expenseflow.data.local.entity.Account
import com.uansari.expenseflow.data.local.entity.Category
import com.uansari.expenseflow.domain.model.AccountType
import com.uansari.expenseflow.domain.model.TransactionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseCallback(
    private val scope: CoroutineScope,
    private val accountDao: () -> AccountDao,
    private val categoryDao: () -> CategoryDao
) : RoomDatabase.Callback() {

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        scope.launch(Dispatchers.IO) {
            populateDefaultAccounts()
            populateDefaultCategories()
        }
    }

    private suspend fun populateDefaultAccounts() {
        val accounts = listOf(
            Account(
                name = "Cash", type = AccountType.CASH, balance = 0.0, color = 0xFF4CAF50, // Green
                icon = "💵", isDefault = true, createdAt = System.currentTimeMillis()
            ), Account(
                name = "Bank Account",
                type = AccountType.BANK,
                balance = 0.0,
                color = 0xFF2196F3, // Blue
                icon = "🏦",
                isDefault = false,
                createdAt = System.currentTimeMillis()
            ), Account(
                name = "Credit Card",
                type = AccountType.CREDIT_CARD,
                balance = 0.0,
                color = 0xFFF44336, // Red
                icon = "💳",
                isDefault = false,
                createdAt = System.currentTimeMillis()
            )
        )
        accountDao().insertAll(accounts)
    }

    private suspend fun populateDefaultCategories() {
        val currentTime = System.currentTimeMillis()

        val expenseCategories = listOf(
            Category(
                name = "Food & Dining", icon = "🍔", color = 0xFFFF6B6B, // Red
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Transportation", icon = "🚗", color = 0xFF4ECDC4, // Teal
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Shopping", icon = "🛒", color = 0xFF45B7D1, // Blue
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Entertainment", icon = "🎬", color = 0xFF96CEB4, // Green
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Bills & Utilities", icon = "💡", color = 0xFFFFEAA7, // Yellow
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Health", icon = "🏥", color = 0xFFDDA0DD, // Plum
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Education", icon = "📚", color = 0xFF98D8C8, // Mint
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Travel", icon = "✈️", color = 0xFFF7DC6F, // Gold
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Other", icon = "📦", color = 0xFFBDC3C7, // Gray
                type = TransactionType.EXPENSE, isDefault = true, createdAt = currentTime
            )
        )

        val incomeCategories = listOf(
            Category(
                name = "Salary", icon = "💰", color = 0xFF2ECC71, // Green
                type = TransactionType.INCOME, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Freelance", icon = "💼", color = 0xFF3498DB, // Blue
                type = TransactionType.INCOME, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Investment", icon = "📈", color = 0xFF9B59B6, // Purple
                type = TransactionType.INCOME, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Gift", icon = "🎁", color = 0xFFE74C3C, // Red
                type = TransactionType.INCOME, isDefault = true, createdAt = currentTime
            ), Category(
                name = "Other", icon = "💵", color = 0xFF95A5A6, // Gray
                type = TransactionType.INCOME, isDefault = true, createdAt = currentTime
            )
        )

        categoryDao().insertAll(expenseCategories + incomeCategories)
    }
}
