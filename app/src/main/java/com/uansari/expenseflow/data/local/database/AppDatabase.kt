package com.uansari.expenseflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.uansari.expenseflow.data.local.dao.AccountDao
import com.uansari.expenseflow.data.local.dao.CategoryDao
import com.uansari.expenseflow.data.local.dao.TransactionDao
import com.uansari.expenseflow.data.local.entity.Account
import com.uansari.expenseflow.data.local.entity.Category
import com.uansari.expenseflow.data.local.entity.Transaction

@Database(
    entities = [Account::class, Category::class, Transaction::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun accountDao(): AccountDao

    abstract fun categoryDao(): CategoryDao

    abstract fun transactionDao(): TransactionDao

    companion object {
        const val DATABASE_NAME = "expense_flow_db"
    }
}