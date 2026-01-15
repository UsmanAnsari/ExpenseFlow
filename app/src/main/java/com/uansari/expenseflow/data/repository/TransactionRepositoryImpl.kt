package com.uansari.expenseflow.data.repository

import com.uansari.expenseflow.data.local.dao.TransactionDao
import com.uansari.expenseflow.data.mapper.toDomain
import com.uansari.expenseflow.data.mapper.toEntity
import com.uansari.expenseflow.domain.model.TransactionDom
import com.uansari.expenseflow.domain.model.TransactionType
import com.uansari.expenseflow.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val transactionDao: TransactionDao
) : TransactionRepository {


    override fun getAllTransactions(): Flow<List<TransactionDom>> {
        return transactionDao.getAllTransactions().map { list -> list.toDomain() }
    }

    override fun getTransactionsByType(
        type: TransactionType
    ): Flow<List<TransactionDom>> {
        return transactionDao.getTransactionsByType(type.name).map { list -> list.toDomain() }
    }

    override fun getTransactionsByDateRange(
        startDate: Long, endDate: Long
    ): Flow<List<TransactionDom>> {
        return transactionDao.getTransactionsByDateRange(
            startDate = startDate, endDate = endDate
        ).map { list -> list.toDomain() }
    }

    override fun getRecentTransactions(
        limit: Int
    ): Flow<List<TransactionDom>> {
        return transactionDao.getRecentTransactions(limit).map { list -> list.toDomain() }
    }


    override suspend fun getTransactionById(id: Long): TransactionDom? {
        return transactionDao.getTransactionById(id)?.toDomain()
    }


    override fun getTotalByTypeAndDateRange(
        type: TransactionType, startDate: Long, endDate: Long
    ): Flow<Double> {
        return transactionDao.getTotalByTypeAndDateRange(
            type = type.name, startDate = startDate, endDate = endDate
        ).map { it ?: 0.0 }
    }

    override fun getSpendingByCategory(
        startDate: Long, endDate: Long
    ): Flow<Map<Long, Double>> {
        return transactionDao.getSpendingByCategory(
            startDate = startDate, endDate = endDate
        ).map { list ->
            // Convert List<CategorySpending> to Map<categoryId, total>
            list.filter { it.categoryId != null }.associate { it.categoryId!! to it.total }
        }
    }


    override suspend fun insertTransaction(
        transaction: TransactionDom
    ): Long {
        return transactionDao.insert(transaction.toEntity())
    }

    override suspend fun updateTransaction(transaction: TransactionDom) {
        transactionDao.update(transaction.toEntity())
    }

    override suspend fun deleteTransaction(transaction: TransactionDom) {
        transactionDao.delete(transaction.toEntity())
    }
}