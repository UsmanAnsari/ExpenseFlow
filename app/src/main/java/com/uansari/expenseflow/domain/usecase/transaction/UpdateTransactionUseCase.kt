package com.uansari.expenseflow.domain.usecase.transaction

import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.AccountRepository
import com.uansari.expenseflow.domain.repository.CategoryRepository
import com.uansari.expenseflow.domain.repository.TransactionRepository
import javax.inject.Inject

class UpdateTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository,
    private val categoryRepository: CategoryRepository
) {

    suspend operator fun invoke(
        transactionId: Long,
        amount: Double,
        type: TransactionType,
        accountId: Long,
        categoryId: Long,
        note: String?,
        date: Long
    ): Result<Unit> {

        if (amount <= 0) {
            return Result.failure(
                IllegalArgumentException("Amount must be positive")
            )
        }

        // Get old transaction
        val oldTransaction =
            transactionRepository.getTransactionById(transactionId) ?: return Result.failure(
                IllegalArgumentException("Transaction not found")
            )

        // Get new account
        val newAccount = accountRepository.getAccountById(accountId) ?: return Result.failure(
            IllegalArgumentException("Account not found")
        )

        val category = categoryRepository.getCategoryById(categoryId) ?: return Result.failure(
            IllegalArgumentException("Category not found")
        )

        return runCatching {

            // ───────────────────────────────────────────────────────────
            // Step 1: Reverse OLD transaction effect
            // ───────────────────────────────────────────────────────────
            oldTransaction.account?.let { oldAccount ->
                val reversedBalance = when (oldTransaction.type) {
                    TransactionType.INCOME -> oldAccount.balance - oldTransaction.amount

                    TransactionType.EXPENSE -> oldAccount.balance + oldTransaction.amount
                }
                accountRepository.updateBalance(
                    oldAccount.id, reversedBalance
                )
            }

            val updatedTransaction = oldTransaction.copy(
                amount = amount,
                type = type,
                account = newAccount,
                category = category,
                note = note?.trim(),
                date = date,
                updatedAt = System.currentTimeMillis()
            )
            transactionRepository.updateTransaction(updatedTransaction)

            val currentAccount = accountRepository.getAccountById(accountId)!!

            val newBalance = when (type) {
                TransactionType.INCOME -> currentAccount.balance + amount

                TransactionType.EXPENSE -> currentAccount.balance - amount
            }
            accountRepository.updateBalance(accountId, newBalance)
        }
    }
}