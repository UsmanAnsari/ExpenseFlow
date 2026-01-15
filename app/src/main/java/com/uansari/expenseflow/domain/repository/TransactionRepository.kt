package com.uansari.expenseflow.domain.repository;

import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {


    fun getAllTransactions(): Flow<List<TransactionDom>>

    fun getTransactionsByType(
        type: TransactionType
    ): Flow<List<TransactionDom>>

    fun getTransactionsByDateRange(
        startDate: Long,
        endDate: Long
    ): Flow<List<TransactionDom>>

    fun getRecentTransactions(limit: Int): Flow<List<TransactionDom>>


    suspend fun getTransactionById(id: Long): TransactionDom?


    fun getTotalByTypeAndDateRange(
        type: TransactionType,
        startDate: Long,
        endDate: Long
    ): Flow<Double>

    fun getSpendingByCategory(
        startDate: Long,
        endDate: Long
    ): Flow<Map<Long, Double>>

    suspend fun insertTransaction(transaction: TransactionDom): Long

    suspend fun updateTransaction(transaction: TransactionDom)

    suspend fun deleteTransaction(transaction: TransactionDom)
}