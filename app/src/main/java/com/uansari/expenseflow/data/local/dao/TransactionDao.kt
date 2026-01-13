package com.uansari.expenseflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.uansari.expenseflow.data.local.entity.Transaction
import com.uansari.expenseflow.data.local.entity.TransactionWithDetails
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long


    @Update
    suspend fun update(transaction: Transaction)


    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC, created_at DESC")
    fun getAllTransactions(): Flow<List<TransactionWithDetails>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC, created_at DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionWithDetails>>

    @androidx.room.Transaction
    @Query(
        """
        SELECT * FROM transactions 
        WHERE date >= :startDate AND date <= :endDate 
        ORDER BY date DESC, created_at DESC
    """
    )
    fun getTransactionsByDateRange(
        startDate: Long, endDate: Long
    ): Flow<List<TransactionWithDetails>>

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionWithDetails?

    @androidx.room.Transaction
    @Query("SELECT * FROM transactions ORDER BY date DESC, created_at DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int): Flow<List<TransactionWithDetails>>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE type = :type AND date >= :startDate AND date <= :endDate
    """
    )
    fun getTotalByTypeAndDateRange(
        type: String, startDate: Long, endDate: Long
    ): Flow<Double>

    @Query(
        """
        SELECT category_id, SUM(amount) as total 
        FROM transactions 
        WHERE type = 'EXPENSE' AND date >= :startDate AND date <= :endDate 
        GROUP BY category_id 
        ORDER BY total DESC
    """
    )
    fun getSpendingByCategory(
        startDate: Long, endDate: Long
    ): Flow<List<CategorySpending>>


    @Query("SELECT COUNT(*) FROM transactions")
    suspend fun getTransactionCount(): Int

    @Query("SELECT COUNT(*) FROM transactions WHERE account_id = :accountId")
    suspend fun getTransactionCountForAccount(accountId: Long): Int
}

data class CategorySpending(
    val categoryId: Long?, val total: Double
)

