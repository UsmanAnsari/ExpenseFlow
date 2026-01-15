package com.uansari.expenseflow.domain.usecase.transaction

import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetTransactionsUseCase @Inject constructor(
    private val transactionRepository: TransactionRepository
) {

    operator fun invoke(
        type: TransactionType? = null, startDate: Long? = null, endDate: Long? = null
    ): Flow<List<TransactionDom>> {

        return when {
            // Filter by both type and date range
            type != null && startDate != null && endDate != null -> {
                transactionRepository.getTransactionsByDateRange(
                    startDate = startDate, endDate = endDate
                ).map { transactions ->
                    transactions.filter { it.type == type }
                }
            }

            type != null -> {
                transactionRepository.getTransactionsByType(type)
            }

            startDate != null && endDate != null -> {
                transactionRepository.getTransactionsByDateRange(
                    startDate = startDate, endDate = endDate
                )
            }

            else -> {
                transactionRepository.getAllTransactions()
            }
        }
    }
}