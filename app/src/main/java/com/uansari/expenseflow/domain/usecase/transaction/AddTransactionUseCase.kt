package com.uansari.expenseflow.domain.usecase.transaction

import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.AccountRepository
import com.uansari.expenseflow.domain.repository.CategoryRepository
import com.uansari.expenseflow.domain.repository.TransactionRepository
import javax.inject.Inject

class AddTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository,
) {

    suspend operator fun invoke(
        amount: Double,
        type: TransactionType,
        accountId: Long,
        categoryId: Long,
        note: String?,
        date: Long
    ): Result<Long> {

        // Validate
        require(amount > 0) { "Amount must be positive" }

        // Get account
        val account = accountRepository.getAccountById(accountId) ?: return Result.failure(
            IllegalArgumentException("Account not found")
        )
        val category = categoryRepository.getCategoryById(categoryId) ?: return Result.failure(
            IllegalArgumentException("Category not found")
        )

        return runCatching {
            // Create and save transaction
            val transaction = TransactionDom(
                amount = amount,
                type = type,
                account = account,
                category = category,
                note = note?.trim(),
                date = date
            )

            val id = transactionRepository.insertTransaction(transaction)

            // Update balance
            val newBalance = when (type) {
                TransactionType.INCOME -> account.balance + amount
                TransactionType.EXPENSE -> account.balance - amount
            }
            accountRepository.updateBalance(accountId, newBalance)

            id
        }
    }
}