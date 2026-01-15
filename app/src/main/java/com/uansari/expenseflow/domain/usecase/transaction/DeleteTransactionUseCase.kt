package com.uansari.expenseflow.domain.usecase.transaction

import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.AccountRepository
import com.uansari.expenseflow.domain.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val accountRepository: AccountRepository
) {

    suspend operator fun invoke(transactionId: Long): Result<Unit> {

        val transaction =
            transactionRepository.getTransactionById(transactionId) ?: return Result.failure(
                IllegalArgumentException("Transaction not found")
            )

        return runCatching {

            transaction.account?.let { account ->
                val newBalance = when (transaction.type) {
                    TransactionType.INCOME -> {
                        // Was income, so subtract to reverse
                        account.balance - transaction.amount
                    }

                    TransactionType.EXPENSE -> {
                        // Was expense, so add back to reverse
                        account.balance + transaction.amount
                    }
                }
                accountRepository.updateBalance(account.id, newBalance)
            }

            transactionRepository.deleteTransaction(transaction)
        }
    }
}